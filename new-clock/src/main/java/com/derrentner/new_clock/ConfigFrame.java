package com.derrentner.new_clock;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class ConfigFrame extends JFrame
{
	private JSlider redSlider, greenSlider, blueSlider;
	private JSlider hueSlider, satSlider, valSlider;
	private JTabbedPane tabs;
	private JLabel previewLabel;
	private JPanel colorPreview;
	private JPanel mainPanel;
	private JPanel titleBar;
	private JButton applyButton;
	private JButton themeButton;
	private JButton closeButton;
	private Point dragOffset;
	private ClockFrame[] clocks;
	private Color titleBarColor = new Color(47, 47, 47);
	private boolean darkMode = true;

	public ConfigFrame(ClockFrame[] clocks)
	{
		this.clocks = clocks;
		
		// Window settings
		setUndecorated(true);
		setSize(300, 300);
		setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15)); // Round Edges Size
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		// Custom title bar
		titleBar = new JPanel(new BorderLayout());
		titleBar.setPreferredSize(new Dimension(0, 20));
		titleBar.setOpaque(true);
		titleBar.setBackground(titleBarColor);
		titleBar.setMinimumSize(new Dimension(0, 20));
		titleBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

		titleBar.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				dragOffset = e.getPoint();
			}
		});

		titleBar.addMouseMotionListener(new MouseAdapter()
		{
			@Override
			public void mouseDragged(MouseEvent e)
			{
				Point currentScreenPos = e.getLocationOnScreen();

				setLocation(currentScreenPos.x - dragOffset.x, currentScreenPos.y - dragOffset.y);
			}
		});

		// Dark/Light Switch
		themeButton = new JButton();
		themeButton.setPreferredSize(new Dimension(16, 16));
		themeButton.setFocusable(false);
		themeButton.setBorderPainted(false);
		themeButton.setContentAreaFilled(false);
		themeButton.setOpaque(false);
		themeButton.setMargin(new Insets(0, 0, 0, 0));
		themeButton.setFocusPainted(false);
		themeButton.setIcon(new ImageIcon(getClass().getResource("pics/dark_mode.png")));

		themeButton.addActionListener(e -> {
			darkMode = !darkMode;
			if (darkMode)
				themeButton.setIcon(new ImageIcon(getClass().getResource("pics/light_mode.png")));
			else
				themeButton.setIcon(new ImageIcon(getClass().getResource("pics/dark_mode.png")));
			applyTheme();
		});

		// Exit Button
		closeButton = new JButton();

		closeButton.setFocusPainted(false);
		closeButton.setBorderPainted(false);
		closeButton.setContentAreaFilled(false);
		closeButton.setOpaque(false);
		closeButton.setMargin(new Insets(0, 0, 0, 0));
		closeButton.setPreferredSize(new Dimension(16, 16));
		closeButton.setFocusable(false);
		closeButton.setOpaque(false);
		closeButton.setIcon(new ImageIcon(getClass().getResource("pics/exit.png")));

		closeButton.addActionListener(e -> dispose());

		JPanel titleButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
		titleButtons.setOpaque(false);

		titleButtons.add(themeButton);
		titleButtons.add(closeButton);

		titleBar.add(titleButtons, BorderLayout.EAST);

		add(titleBar, BorderLayout.NORTH);
		
		mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		// RGB Panel
		Color color = clocks[0].getColor();

		// ===== PREVIEW =====
		JPanel previewPanel = new JPanel(new BorderLayout());
		previewPanel.setBackground(Color.BLACK);
		previewPanel.setPreferredSize(new Dimension(200, 60));

		previewLabel = new JLabel("12:34", JLabel.CENTER);
		previewLabel.setFont(previewLabel.getFont().deriveFont(28f));
		previewPanel.add(previewLabel, BorderLayout.CENTER);

		// ===== TABS =====
		tabs = new JTabbedPane();

		// RGB TAB
		JPanel rgbPanel = new JPanel(new GridLayout(3, 1));

		redSlider = createSlider(color.getRed());
		greenSlider = createSlider(color.getGreen());
		blueSlider = createSlider(color.getBlue());

		ChangeListener rgbListener = e -> updatePreview();

		redSlider.addChangeListener(rgbListener);
		greenSlider.addChangeListener(rgbListener);
		blueSlider.addChangeListener(rgbListener);

		rgbPanel.add(labeledSlider("R", redSlider));
		rgbPanel.add(labeledSlider("G", greenSlider));
		rgbPanel.add(labeledSlider("B", blueSlider));

		tabs.addTab("RGB", rgbPanel);

		// HSV TAB
		JPanel hsvPanel = new JPanel(new GridLayout(3, 1));

		float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

		hueSlider = new JSlider(0, 360, (int)(hsv[0] * 360));
		satSlider = new JSlider(0, 100, (int)(hsv[1] * 100));
		valSlider = new JSlider(0, 100, (int)(hsv[2] * 100));

		ChangeListener hsvListener = e -> updatePreview();

		hueSlider.addChangeListener(hsvListener);
		satSlider.addChangeListener(hsvListener);
		valSlider.addChangeListener(hsvListener);

		hsvPanel.add(labeledSlider("H", hueSlider));
		hsvPanel.add(labeledSlider("S", satSlider));
		hsvPanel.add(labeledSlider("V", valSlider));

		tabs.addTab("HSV", hsvPanel);

		// ===== WRAP =====
		JPanel center = new JPanel(new BorderLayout(10, 10));
		center.add(previewPanel, BorderLayout.NORTH);
		center.add(tabs, BorderLayout.CENTER);

		mainPanel.add(center, BorderLayout.CENTER);

		// Apply Button
		JPanel applyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		applyButton = new JButton("Apply");
		applyButton.setPreferredSize(new Dimension(120, 28));
		applyButton.setBackground(new Color(60, 60, 60));
		applyButton.setForeground(Color.WHITE);
		applyButton.setFocusPainted(false);
		applyButton.addActionListener(e -> applyColor());

		applyPanel.add(applyButton);

		mainPanel.add(applyPanel, BorderLayout.SOUTH);

		// Rest
		add(mainPanel, BorderLayout.CENTER);

		applyTheme();
	}
	
	private JSlider createSlider(int value)
	{
	    JSlider slider = new JSlider(0, 255, value);
	    slider.setMajorTickSpacing(85);
	    slider.setPaintTicks(true);
	    slider.setPaintLabels(true);
	    return slider;
	}
	
	private void updatePreview()
	{
	    Color c = getCurrentColor();

	    previewLabel.setForeground(c);

	    // optional but HIGHLY recommended: contrast fix
	    float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
	    previewLabel.setBackground(hsb[2] < 0.5 ? Color.WHITE : Color.BLACK);
	}
	
	private JPanel labeledSlider(String label, JSlider slider)
	{
	    JPanel panel = new JPanel(new BorderLayout());
	    panel.add(new JLabel(label), BorderLayout.WEST);
	    panel.add(slider, BorderLayout.CENTER);
	    return panel;
	}

//	private void styleField(JTextField field)
//	{
//	    field.setBorder(BorderFactory.createCompoundBorder(
//	        BorderFactory.createLineBorder(new Color(120,120,120)),
//	        BorderFactory.createEmptyBorder(2,4,2,4)
//	    ));
//	}
//	
	private void updateComponentColors(Container container, Color background, Color foreground)
	{

		container.setBackground(background);
		container.setForeground(foreground);

		for (Component component : container.getComponents())
		{

			if (component == titleBar)
				continue;
			component.setBackground(background);
			component.setForeground(foreground);

			if (component instanceof Container child)
				updateComponentColors(child, background, foreground);
		}
	}

	private Color getCurrentColor()
	{
	    if (tabs.getSelectedIndex() == 0)
	    {
	        return new Color(
	            redSlider.getValue(),
	            greenSlider.getValue(),
	            blueSlider.getValue()
	        );
	    }
	    else
	    {
	        return Color.getHSBColor(
	            hueSlider.getValue() / 360f,
	            satSlider.getValue() / 100f,
	            valSlider.getValue() / 100f
	        );
	    }
	}
	
	private void applyTheme()
	{
		Color background;
		Color foreground;

		if (darkMode)
		{
			background = new Color(40, 40, 40);
			foreground = Color.WHITE;

		} else
		{
			background = Color.WHITE;
			foreground = Color.BLACK;
		}

		updateComponentColors(getContentPane(), background, foreground);

		repaint();
	}

	private void applyColor()
	{
	    Color color = getCurrentColor();

	    clocks[0].updateColor(color);
	}
}