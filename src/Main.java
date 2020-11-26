import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
 
public class Main extends JFrame {
 
	private static final long serialVersionUID = 1L;
	
	private int MAX_ITER = 570;
    private double ZOOM = 300;
    private BufferedImage I;
    private double zx, zy, cX, cY, tmp;
    private int sizex = Toolkit.getDefaultToolkit().getScreenSize().width, sizey = Toolkit.getDefaultToolkit().getScreenSize().height;
    private double verschiebungx = -1.7489981470644, verschiebungy = 0.0;
    private int waitms = 1000;
    private int loop = 0;
    private String coloralgorithm = "gray";
    
    private JProgressBar progress, frameprogress;
    private JFrame dia;
    private Task task;
    
    private Integer paletteLength = 10;
    
    class Task extends SwingWorker<Object, Object> {
    	private int pro;
    	private double multiplier;
    	private int frames;
    	private String saveDir;
    	
    	public Task(double multiplier, int frames, String saveDir) {
    		this.multiplier = multiplier;
    		this.frames = frames;
    		this.saveDir = saveDir;
    	}

    	public Object doInBackground() {
    		if(frames > 1) {
    			double localzoom = ZOOM;
    			
    			if(saveDir.equals("")) {
        			
        			BufferedImage[] images = new BufferedImage[frames];
        			
        			progress.setMaximum(frames);
        			
        			
        			for (int i = 0; i < frames; i++) {
        				pro = i;
        				images[i] = generateFrame(localzoom, verschiebungx, verschiebungy);
        				localzoom *= multiplier;
        				progress.setValue(i);
        				progress.setString(Integer.toString(i) + " / " + Integer.toString(frames));
        				I = images[i];
        			}
        			
        			JOptionPane.showMessageDialog(null, "FINISHED!!! HIT OK TO START!!!");
        			dia.dispose();
        			
        			Timer timer = new Timer();
        			timer.scheduleAtFixedRate(new TimerTask() {

        				@Override
        				public void run() {
        					if(loop >= frames) {
        						timer.cancel();
        						
        					}else {
        						I = images[loop];
        						loop += 1;
        					}
        				}
        				
        			}, 0, waitms);
    			}else{
    				progress.setMaximum(frames);
    				
        			for (int i = 0; i < frames; i++) {
        				BufferedImage tempIm = generateFrame(localzoom, verschiebungx, verschiebungy);
        				try {
							ImageIO.write(tempIm, "jpg", new File(saveDir + Integer.toString(i) + ".jpg"));
						} catch (IOException e) {
							JOptionPane.showMessageDialog(null, "COULDNT SAVE FRAME");
							e.printStackTrace();
						}
        				pro = i;
        				localzoom *= multiplier;
        				progress.setValue(i);
        				progress.setString(Integer.toString(i) + " / " + Integer.toString(frames));
        				I = tempIm;
        			}
        			
        			JOptionPane.showMessageDialog(null, "FINISHED!!! HIT OK TO START!!!");
        			dia.dispose();
        			
        			Timer timer = new Timer();
        			timer.scheduleAtFixedRate(new TimerTask() {

        				@Override
        				public void run() {
        					if(loop >= frames) {
        						timer.cancel();
        						
        					}else {
        						try {
									I = ImageIO.read(new File(saveDir + Integer.toString(loop) + ".jpg"));
								} catch (IOException e) {
									e.printStackTrace();
								}
        						loop += 1;
        					}
        				}
        				
        			}, 0, waitms);
    			}
    			
    			
    		}else {
    			I = generateFrame(ZOOM, verschiebungx, verschiebungy);
    			
    			if(!saveDir.equals("")) {
					try {
						ImageIO.write(I, "jpg", new File(saveDir));
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "COULDNT SAVE FRAME");
						e.printStackTrace();
					}
    			}
    		}
    		
			return null;
    	}
    	
    	protected int process() {
    		return pro;
    	}
    }
    
    public Main() {   	
        super("Mandelbrot Set");
        setBounds(0, 0, sizex, sizey);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        sizex = getWidth();
        sizey = getHeight();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        requestFocus();
        
		I = generateFrame(ZOOM, verschiebungx, verschiebungy);
		
		setIconImage(I);
        
        addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_C) {
					showSettingScreen();
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
        	
        });
        
        addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				verschiebungx += (e.getX() - (getWidth()/2)) / ZOOM;
				verschiebungy += (e.getY() - (getHeight()/2)) / ZOOM;
				showSettingScreen();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
        	
        });
    }
 
    @Override
    public void paint(Graphics g) {  	
    	g.drawImage(I, 0, 0, null);
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(Double.toString(ZOOM), 300, 300);
        repaint();
    }
    
    public BufferedImage generateFrame(double localzoom, double verschiebungx2, double verschiebungy2) {
    	if(frameprogress != null) {
    		frameprogress.setMinimum(0);
        	frameprogress.setMaximum(getHeight() * getWidth());
        	frameprogress.setValue(0);
    	}
    	
    	ZOOM = localzoom;
    	verschiebungx = verschiebungx2;
    	verschiebungy = verschiebungy2;
    	BufferedImage buf = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
    	
    	for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                zx = zy = 0;
              cX = (x - ((sizex)/2)) / localzoom + verschiebungx2;
              cY = (y - ((sizey/2))) / localzoom + verschiebungy2;
              int iter = MAX_ITER;
              
              while ((zx * zx + zy * zy < 4) && (iter > 0)) {
                  tmp = zx * zx - zy * zy + cX;
                  zy = zx * 2.0 * zy + cY;
                  zx = tmp;
                  iter--;
              }
              
              if(coloralgorithm.equals("blue")) {
                  buf.setRGB(x, y, ((iter) | (iter << 8)));
              }else if(coloralgorithm.equals("second")){
                  int value = 255;
                  buf.setRGB(x, y, (int) ((int)(Math.pow(255, 3) * (MAX_ITER - iter) / MAX_ITER / paletteLength) + Math.pow(255, 2) + value));
              }else if(coloralgorithm.equals("hsb")) {
              	if(iter == 0) {
              		buf.setRGB(x, y, 0);
              	} else {
              		buf.setRGB(x, y, Color.HSBtoRGB((float)((MAX_ITER - iter) / MAX_ITER) * 10, 0.5f, 1));
              	}
              }else if(coloralgorithm.equals("gray")) {
            	  buf.setRGB(x, y, (int) ((Math.pow(255, 3)*(MAX_ITER - iter) + Math.pow(255, 2)*(MAX_ITER - iter) + 255 * (MAX_ITER - iter) + (MAX_ITER - iter)))/paletteLength);
            	  if(iter == 0 || iter == MAX_ITER) buf.setRGB(x, y, 0);
              }
              
              if(frameprogress != null) {
            	  frameprogress.setValue(y * getWidth() + x);
            	  frameprogress.setString(Integer.toString(y * getWidth() + x) + " / " + Integer.toString(getHeight() * getWidth()));
              }
            }
		}
        
        return buf;
    }
    
    private void showSettingScreen() {
		dia = new JFrame();
		dia.setTitle("Settings");
		dia.setLayout(null);
		dia.setBounds(100, 100, 550, 500);
		
		JTextField multiplier = new JTextField(Double.toString(ZOOM));
		multiplier.setBounds(new Rectangle(20,50,100,20));
		dia.add(multiplier);
		
		JTextField frames = new JTextField();
		frames.setBounds(new Rectangle(120, 50, 100, 20));
		dia.add(frames);
		
		JTextField verschiebungxfield = new JTextField(Double.toString(verschiebungx));
		verschiebungxfield.setBounds(new Rectangle(250,50,100,20));
		dia.add(verschiebungxfield);
		
		JTextField verschiebungyfield = new JTextField(Double.toString(verschiebungy));
		verschiebungyfield.setBounds(new Rectangle(350,50,100,20));
		dia.add(verschiebungyfield);
		
		JCheckBox singleFrame = new JCheckBox();
		singleFrame.setBounds(new Rectangle(20,70,20,20));
		singleFrame.setSelected(true);
		dia.add(singleFrame);
		
		JLabel singleInfo = new JLabel("Single Frame");
		singleInfo.setBounds(50, 70, 300, 20);
		dia.add(singleInfo);
		
		frameprogress = new JProgressBar();
		frameprogress.setMinimum(0);
		frameprogress.setMaximum(100);
		frameprogress.setValue(0);
		frameprogress.setStringPainted(true);
		frameprogress.setForeground(Color.GREEN);
		frameprogress.setBounds(new Rectangle(20,110,300,20));
		dia.add(frameprogress);
		
		progress = new JProgressBar();
		progress.setMinimum(0);
		progress.setMaximum(100);
		progress.setValue(0);
		progress.setStringPainted(true);
		progress.setForeground(Color.GREEN);
		progress.setBounds(new Rectangle(20,150,300,20));
		dia.add(progress);
		
		JTextField maxiterations = new JTextField(Integer.toString(MAX_ITER));
		maxiterations.setBounds(20, 200, 100, 20);
		dia.add(maxiterations);
		
		ButtonGroup group = new ButtonGroup();
		
		JRadioButton blue = new JRadioButton("Blue Color Model");
		blue.setBounds(20, 250, 200, 20);
		group.add(blue);
		dia.add(blue);
		
		JRadioButton second = new JRadioButton("Second Color Model");
		second.setBounds(20, 270, 200, 20);
		group.add(second);
		dia.add(second);
		
		JRadioButton hsb = new JRadioButton("HSB Color Model");
		hsb.setBounds(20, 290, 200, 20);
		group.add(hsb);
		dia.add(hsb);
		
		JRadioButton gray = new JRadioButton("Red Color Model");
		gray.setBounds(20, 310, 200, 20);
		group.add(gray);
		dia.add(gray);
		
		if(coloralgorithm.equals("blue")) {
			blue.setSelected(true);
		}else if(coloralgorithm.equals("second")){
			second.setSelected(true);
		}else if(coloralgorithm.equals("hsb")) {
			hsb.setSelected(true);
		}else if(coloralgorithm.equals("gray")) {
			gray.setSelected(true);
		}
		
		JTextField length = new JTextField(Integer.toString(paletteLength));
		length.setBounds(250, 270, 200, 20);
		dia.add(length);
		
		JTextField waitfield = new JTextField(Integer.toString(waitms));
		waitfield.setBounds(120, 200, 100, 20);
		dia.add(waitfield);
		
		JCheckBox save = new JCheckBox();
		save.setBounds(20, 330, 20, 20);
		dia.add(save);
		
		JLabel saveInfo = new JLabel("Save Frame As");
		saveInfo.setBounds(50, 330, 300, 20);
		dia.add(saveInfo);
		
		JTextField dir = new JTextField();
		dir.setBounds(20, 350, 500, 20);
		dia.add(dir);
		
		
		
		JButton submit = new JButton("Compute");
		submit.setBounds(new Rectangle(20,90,100,20));
		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent x) {
				MAX_ITER = Integer.parseInt(maxiterations.getText());
				verschiebungx = Double.parseDouble(verschiebungxfield.getText());
				verschiebungy = Double.parseDouble(verschiebungyfield.getText());
				paletteLength = Integer.parseInt(length.getText());
				
				if(blue.isSelected()) {
					coloralgorithm = "blue";
				}else if(second.isSelected()){
					coloralgorithm = "second";
				}else if(hsb.isSelected()) {
					coloralgorithm = "hsb";
				}else if(gray.isSelected()) {
					coloralgorithm = "gray";
				}
				
				if(!singleFrame.isSelected()) {
					waitms = Integer.parseInt(waitfield.getText());
					
					if(save.isSelected()) {
						task = new Task(Double.parseDouble(multiplier.getText()), Integer.parseInt(frames.getText()), dir.getText());
					}else {
						task = new Task(Double.parseDouble(multiplier.getText()), Integer.parseInt(frames.getText()), "");
					}
					
					task.addPropertyChangeListener(new PropertyChangeListener() {

						@Override
						public void propertyChange(PropertyChangeEvent x) {
							progress.setValue(task.process());
							progress.setString(Double.toString(100*progress.getPercentComplete()) + "%");
						}
						
					});
					task.execute();
				}else {
					ZOOM = Double.parseDouble(multiplier.getText());
					
					if(save.isSelected()) {
						task = new Task(1, 1, dir.getText());
					}else {
						task = new Task(1, 1, "");
					}
					
					task.execute();
				}
			}						
		});
		dia.add(submit);
		
		dia.setVisible(true);
    }
 
    public static void main(String[] args) {
        new Main().setVisible(true);
    }
}