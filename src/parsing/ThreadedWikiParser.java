package parsing;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

public abstract class ThreadedWikiParser extends XMLParser {
	
	private int thread_number  		= 2;
	protected int pages_by_thread 	= 1;
	private int queue_capacity 		= 2000;
	private long maximum_pages 		= Long.MAX_VALUE;
	
	private Thread[] thread_pool;
	private Writer thread_writer;
	private ArrayBlockingQueue<Page[]> dispatch;
	protected ArrayBlockingQueue<Page[]> retrieve;
	
	protected static volatile int loop_test = 0;
	
	protected ThreadedWikiParser(File input) {
		super(input);
		thread_pool = new Thread[thread_number];
		for(int i = 0; i < thread_number; i++)
			thread_pool[i] = new Worker();
		thread_writer = new Writer();
		dispatch = new ArrayBlockingQueue<Page[]>(queue_capacity);
		retrieve = new ArrayBlockingQueue<Page[]>(thread_number*queue_capacity);
	}
	
	protected ThreadedWikiParser(File input, int thr_num) {
		super(input);
		thread_number = thr_num;
		thread_pool = new Thread[thread_number];
		for(int i = 0; i < thread_number; i++)
			thread_pool[i] = new Worker();
		thread_writer = new Writer();
		dispatch = new ArrayBlockingQueue<Page[]>(queue_capacity);
		retrieve = new ArrayBlockingQueue<Page[]>(thread_number*queue_capacity);
	}
	
	private class Worker extends Thread{
		public void run(){
			
			while(true){
				
				Page[] pages = null;
				try { pages = dispatch.take(); } 
					catch (InterruptedException e) { return; }

				if(pages == null)
					break;
				if(pages[0].interrupt)
					break;
				
				//System.out.println("worker loop : "+(loop_test++));
				
				for(int i = 0; i < pages_by_thread; i++){
					
					Page p = pages[i];
					LinkedList<String> l = new LinkedList<String>();
					LinkedList<String> formatted_l = new LinkedList<String>();
					if(p == null)
						break;
					
					/* Method 2 */
					String[] tag1 = getTagContents("title", p.raw);
					String[] tag2 = getTagContents("text", tag1[1]);
					p.title = tag1[0];
					p.text  = tag2[0];
					
					
					try { l = getContents("[[", "]]",p.text); } catch (IOException e) { e.printStackTrace(); }
					
					while(l.size() > 0){
						String s = properLinks(l.remove());
						if(s.compareTo("") != 0)
							formatted_l.add(s);
					}
					p.formatted_links = formatted_l;
					p.text = null;
					p.title = properLinks(p.title);
					l = null;
					
				}
				try { retrieve.put(pages); } 
					catch (InterruptedException e) { e.printStackTrace(); }
			}
			
		}
	}
	
	private class Writer extends Thread{
		public void run(){
			retrieval_method();
		}
	}
	
	protected abstract void retrieval_method();
	
	public void setPPT(int ppt){
		if(ppt > 0)
			pages_by_thread = ppt;
	}
	
	public void setCapacity(int c){
		if(c > 0){
			queue_capacity = c;
			dispatch = new ArrayBlockingQueue<Page[]>(queue_capacity);
			retrieve = new ArrayBlockingQueue<Page[]>(thread_number*queue_capacity);
		}
	}
	
	public void setMaxPages(long maxp){
		maximum_pages = maxp;
	}
	
	
	protected ThreadedWikiParser(File input, int thr_num, int capacity) {
		super(input);
		thread_number = thr_num;
		queue_capacity = capacity;
		thread_pool = new Thread[thread_number];
		for(int i = 0; i < thread_number; i++)
			thread_pool[i] = new Worker();
		thread_writer = new Writer();
		dispatch = new ArrayBlockingQueue<Page[]>(queue_capacity);
		retrieve = new ArrayBlockingQueue<Page[]>(thread_number*queue_capacity);
	}
	
	public static String properLinks(String link){
		
		int i = -1;
		link = link.trim();
		
		if(link.startsWith(">"))
			link = link.substring(1);
		
		if(link.contains("#"))
			return "";
		if(link.contains(":"))
			return "";
		if((i = link.indexOf('|')) != -1)
			return link.substring(0, i);
		
		return link;
	}
	
	private void start_threads(){
		for(int i = 0; i < thread_number; i++)
			thread_pool[i].start();
		thread_writer.start();
	}

	private void close_threads() throws InterruptedException{
		Page[] p = new Page[1];
		p[0] = new Page(true);
		for(int i = 0; i < thread_number; i++)
			dispatch.put(p);
		for(int i = 0; i < thread_number; i++)
			thread_pool[i].join();
		
		retrieve.put(p);
		thread_writer.join();
		p[0] = null;
	}
	
	public void parse() {
		
		int i = 0;
		boolean go_on = true;
		start_threads();
		
		while(go_on && i < maximum_pages){
			//System.out.println("dispatcher loop i : "+i);
			
			Page[] pages = new Page[pages_by_thread];
			
			try {
				
				for(int z = 0; z < pages_by_thread; z++){
					/* Method n°1 
					String title   = getTagContent("title");
					if(title == ""){
						go_on = false;
						if(z > 0)
							dispatch.put(pages);
						break;
					}
					String content = getTagContent("text");
					pages[z] = new Page(title,content);
					*/
					
					/* Method n°3 */
					String page = getStartingTags("page");
					/* Method n°2 
					String page = getTagContent("page"); */
					if(page.compareTo("") == 0){
						go_on = false;
						if(z > 0)
							dispatch.put(pages);
						break;
					}
					pages[z] = new Page(page);
				
				}
				
				if(go_on)
					dispatch.put(pages);
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			i += pages_by_thread;
		}
		
		try { close_fd(); close_threads(); } 
			catch (InterruptedException e) { e.printStackTrace(); } 
			catch (IOException e) { e.printStackTrace(); }
		
	}

}
