package gui;

import graph.EfficientGraph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import options.Parameters;
import parsing.FileThreadedWikiParser;
import parsing.Fragment;
import requests.requestParser;
import utils.TextAreaOutputStream;
import utils.Toolz;

@SuppressWarnings("serial")
public class GUI extends JFrame {

	public GUI() {
		build_contents();
	}
	
	private class AutoScrollingTextArea extends JTextArea{
		public void append(String str){
			super.append(str);
			this.setCaretPosition(this.getDocument().getLength()); 
		}
	}

	JPanel northpanel = new JPanel();
	JButton button_step1 = new JButton("Prétraitement ");
	JLabel one_to_2 = new JLabel("~>");
	JButton button_step2 = new JButton("Index & graphe");
	JLabel two_to_3 = new JLabel("~>");
	JButton button_step3 = new JButton("Graphe mémoire");

	JScrollPane leftpanel;
	JList out_list = new JList();

	JScrollPane rightpanel;
	JList in_list = new JList();

	JScrollPane centerpanel;
	AutoScrollingTextArea log = new AutoScrollingTextArea();

	JPanel southpanel = new JPanel();
	JTextField node_in = new JTextField();
	JButton node_button = new JButton("INFO");
	JButton import_button = new JButton("IMPORT");

	EfficientGraph g;

	private void frame_finalize() throws IOException {
		// this.pack();
		this.setIconImage(ImageIO.read(new File(Parameters.icon_path)));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(((dim.width) / 2 - this.getWidth() / 2),
				((dim.height) / 2) - this.getHeight() / 2);
		
		if(Parameters.debug){
			log.append("- DEBUG MODE : ON -\n");
			log.append("\n");
		}
		
		this.setVisible(true);
	}

	private void frame_init() {
		this.setTitle("Wikilinks");
		this.setResizable(false);
		this.setSize(new Dimension(850, 500));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
	}

	private void place_components() {

		northpanel.add(button_step1);
		northpanel.add(one_to_2);
		northpanel.add(button_step2);
		northpanel.add(two_to_3);
		northpanel.add(button_step3);
		this.getContentPane().add(northpanel, BorderLayout.NORTH);

		out_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		out_list.setLayoutOrientation(JList.VERTICAL);
		out_list.setForeground(Color.RED);
		DefaultListModel m = new DefaultListModel();
		m.addElement("Liens sortants");
		out_list.setModel(m);

		leftpanel = new JScrollPane(out_list,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		leftpanel.setPreferredSize(new Dimension(160, 300));
		leftpanel.setMinimumSize(new Dimension(160, 300));

		in_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		in_list.setLayoutOrientation(JList.VERTICAL);
		in_list.setBackground(Color.WHITE);
		in_list.setForeground(Color.BLUE);
		DefaultListModel m2 = new DefaultListModel();
		m2.addElement("Liens entrants");
		in_list.setModel(m2);

		rightpanel = new JScrollPane(in_list,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		rightpanel.setPreferredSize(new Dimension(160, 300));
		rightpanel.setMinimumSize(new Dimension(160, 300));

		centerpanel = new JScrollPane(log);
		log.setForeground(Color.DARK_GRAY);
		log.setFont(new Font("Tahoma", Font.BOLD, 11));
		log.setEditable(false);
		log.setLineWrap(false);
		log.setAutoscrolls(true);
		try {
			System.setOut(new PrintStream(new TextAreaOutputStream(log),true,Parameters.char_encoding));
		} catch (UnsupportedEncodingException e) {
			System.setOut(new PrintStream(new OutputStream(){
				public void write(int i) throws IOException {
					char c = (char) i;
					log.append(Character.toString(c));
				}
			}));
		}
		log.append("WIKILINKS - Bienvenue\n");
		log.append("\n");

		node_in.setPreferredSize(new Dimension(350, 20));
		node_button.setPreferredSize(new Dimension(70, 17));
		import_button.setPreferredSize(new Dimension(70, 17));
		southpanel.add(import_button);
		southpanel.add(node_in);
		southpanel.add(node_button);

		this.getContentPane().add(leftpanel, BorderLayout.WEST);
		this.getContentPane().add(rightpanel, BorderLayout.EAST);
		this.getContentPane().add(centerpanel, BorderLayout.CENTER);
		this.getContentPane().add(southpanel, BorderLayout.SOUTH);
	}

	private void disable_all() {
		node_button.setEnabled(false);
		import_button.setEnabled(false);
		button_step1.setEnabled(false);
		button_step2.setEnabled(false);
		button_step3.setEnabled(false);
	}

	private void enable_all() {
		node_button.setEnabled(true);
		import_button.setEnabled(true);
		button_step1.setEnabled(true);
		button_step2.setEnabled(true);
		button_step3.setEnabled(true);
	}

	private void initial_setup() {

		node_button.setEnabled(false);
		import_button.setEnabled(false);

		File f = new File(Parameters.file_Content);
		if (!f.exists()) {
			log.append("Fichier dump Wikipedia introuvable.\nToutes les fonctions sont suspendues.\nConfigurer l'emplacement dans le fichier options.txt à la racine du programme.\n");
			button_step1.setEnabled(false);
			button_step2.setEnabled(false);
			button_step3.setEnabled(false);
			return;
		}

		File check = new File(Parameters.file_firstStep);
		if (!check.exists()) {
			log.append("Fichiers de prétraitement 1 introuvable.\n");
			button_step2.setEnabled(false);
			button_step3.setEnabled(false);
			return;
		}

		check = new File(Parameters.file_Trimmed);
		File check2 = new File(Parameters.file_Index);
		if (!check.exists() || !check2.exists()) {
			log.append("Index ou fichier graphe introuvable.\n");
			button_step3.setEnabled(false);
			return;
		}

	}

	private void set_listeners() {

		button_step1.addActionListener(new ActionListener() {

			File f = new File(Parameters.file_Content);

			public void actionPerformed(ActionEvent e) {
				disable_all();
				log.append("Démarrage du prétraitement ...\n");
				new Thread() {
					public void run() {
						FileThreadedWikiParser p = new FileThreadedWikiParser(
								f, Parameters.parse_nbThreads);
						p.setPPT(Parameters.parse_PPT);
						p.setCapacity(Parameters.parse_capacity);
						p.parse();
						try {
							SwingUtilities.invokeAndWait(new Runnable() {
								public void run() {
									log.append("Prétraitement terminé.\n");
									enable_all();
									initial_setup();
								}
							});
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
		});

		button_step2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disable_all();
				log.append("Création de l'index et du fichier graphe.\n");
				new Thread() {
					public void run() {
						try {
							Fragment.fragmentFile(Parameters.file_firstStep,
									Parameters.file_Index,
									Parameters.file_Trimmed);
							try {
								SwingUtilities.invokeAndWait(new Runnable() {
									public void run() {
										log.append("Création terminée.\n");
										enable_all();
										initial_setup();
									}
								});
							} catch (InterruptedException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
						} catch (FileNotFoundException ferr) {
							log.append("Erreur, fichier introuvable ou inaccessible ...\n");
							;
						} catch (IOException ioerr) {
							log.append("Erreur d'entrée - sortie.\n");
						}
					}
				}.start();

			}
		});

		button_step3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disable_all();
				log.append("Mise en mémoire du graphe.\n");
				new Thread() {
					public void run() {

						g = new EfficientGraph(Parameters.file_Index,
								Parameters.file_Trimmed);
						g.buildGraph();
						try {
							SwingUtilities.invokeAndWait(new Runnable() {
								public void run() {
									log.append("Etape terminée, les données sont maintenant accessibles.\n");
									enable_all();
									initial_setup();
									node_button.setEnabled(true);
									import_button.setEnabled(true);
								}
							});
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
		});

		node_button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				disable_all();
				final String node_to_check = node_in.getText();
				log.append("Recherche des informations du noeud : "
						+ node_to_check + ".\n");

				new Thread() {
					
					public void run() {
						
						int id_to_check;
						
						try {
							id_to_check = Fragment.getId(node_to_check,
									Parameters.file_Index);

							if (id_to_check == -1) {
								log.append("Noeud inconnu.\n");
								try {
									SwingUtilities.invokeAndWait(new Runnable() {
										public void run() { enable_all(); }
									});
								} 
								catch (InterruptedException e) { e.printStackTrace(); } 
								catch (InvocationTargetException e) { e.printStackTrace(); } 
								finally { enable_all(); }
								return;
							}
						

							int[] links_in = g.get_incoming(id_to_check);
							int[] links_out = Toolz.removeDoublons(g
									.get_outgoing(id_to_check));
							String[] str_in = Fragment.getTitle(links_in,
									Parameters.file_Index);
							String[] str_out = Fragment.getTitle(links_out,
									Parameters.file_Index);

							final DefaultListModel m1 = new DefaultListModel();
							final DefaultListModel m2 = new DefaultListModel();

							if (str_in != null) {
								Arrays.sort(str_in);
								for (String s : str_in)
									m1.addElement(s);
							}
							if (str_out != null) {
								Arrays.sort(str_out);
								for (String s : str_out)
									m2.addElement(s);
							}

							in_list.setModel(m1);
							out_list.setModel(m2);

							str_in = null;
							str_out = null;
							links_in = null;
							links_out = null;

							try {
								SwingUtilities.invokeAndWait(new Runnable() {
									public void run() {
										in_list.setModel(m1);
										out_list.setModel(m2);
										log.append("Recherche terminée pour le noeud : "
												+ node_to_check + ".\n");
										enable_all();
									}
								});
							} catch (InterruptedException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							} finally {
								enable_all();
							}
						} catch (IOException e1) {
							log.append("Erreur I/O.\n");
							return;
						}
					}
				}.start();

			}
		});

		out_list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				node_in.setText((String) out_list.getSelectedValue());
			}
		});

		in_list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				node_in.setText((String) in_list.getSelectedValue());
			}
		});

		import_button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				final JFileChooser fc = new JFileChooser(".");
				int returnVal = fc.showOpenDialog(GUI.this);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					disable_all();
					new Thread(){
						public void run(){
							File requests = fc.getSelectedFile();
							//////////////////////////////////////////
							BufferedReader r_req = null;
							BufferedWriter w_res = null; 
							BufferedWriter w_tim = null;
							try{

								Toolz.getTime("Ouverture du fichier de requêtes ["+requests.getName()+"]  \t@ ");
								r_req = new BufferedReader(new InputStreamReader(new FileInputStream(requests), Parameters.char_encoding));
								Toolz.getTime("Ouverture du fichier de réponses ["+Parameters.output_Responses+"]  \t@ ");
								File responses = new File(Parameters.output_Responses);
								w_res = new BufferedWriter(new FileWriter(responses));
								w_tim = new BufferedWriter(new FileWriter(Parameters.file_Timer));

								String line; int count = 0;

								while((line = r_req.readLine()) != null){
									System.gc();
									long timing = Toolz.bench();
									Toolz.getTime("[Début du lot de requêtes n°"+count+++"]   \t@ ");
									int[] to_write = requestParser.parse_request_group(line, g);
									if(to_write == null){
										w_res.newLine();
										w_tim.newLine();
										continue;
									}
									Toolz.getTime("[Ecriture du lot de requêtes]   \t@ ");
									String[] retrieved = Fragment.getTitle(to_write, Parameters.file_Index);
									for(int i = 0; i < retrieved.length; i++){
										if(i > 0)
											w_res.write(',');
										Toolz.debug_print("<"+retrieved[i]+">");
										w_res.write('<');
										w_res.write(retrieved[i]);
										w_res.write('>');
									}
									Toolz.debug_println("");
									w_res.newLine();
									w_res.flush();
									//Benchmarking
									timing = Toolz.bench();
									w_tim.write(Toolz.format_time(timing)+"\n");
									w_tim.flush();

								}
							} catch(IOException i) {
								i.printStackTrace();
							} finally {
								try {
									r_req.close();
									w_res.close();
									w_tim.close();
									try {
										SwingUtilities.invokeAndWait(new Runnable() {
											public void run() { enable_all(); }
										});
									} 
									catch (Exception e) { e.printStackTrace(); }  
									finally { enable_all(); }
								} catch (IOException ioe) {}
							}
						}}.start();
						//////////////////////////////////////////
				}
			}
		});

	}

	private void build_contents() {
		
		frame_init();
		place_components();
		
		try { Parameters.init_params(); } catch (IOException e) { System.out.println("Erreur lors de la lecture du fichier options."); }
		
		initial_setup();
		set_listeners();
		try { frame_finalize(); } catch (IOException e) { System.out.println("Erreur lors de la finalisation à l'ouverture de la fenêtre graphique.");}
	}

}
