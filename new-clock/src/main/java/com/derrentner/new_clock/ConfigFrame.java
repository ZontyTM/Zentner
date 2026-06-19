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
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ConfigFrame extends JFrame
{
	private JTextField redField;
	private JTextField greenField;
	private JTextField blueField;
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
		setSize(300, 125);
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
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		
		JPanel rgbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

		Color color = clocks[0].getColor();
		
		redField = new JTextField(String.valueOf(color.getRed()), 4);
		styleField(redField);

		greenField = new JTextField(String.valueOf(color.getGreen()), 4);
		styleField(greenField);

		blueField = new JTextField(String.valueOf(color.getBlue()), 4);
		styleField(blueField);
		
		rgbPanel.add(new JLabel("R:"));
		rgbPanel.add(redField);

		rgbPanel.add(new JLabel("G:"));
		rgbPanel.add(greenField);

		rgbPanel.add(new JLabel("B:"));
		rgbPanel.add(blueField);
		

		JPanel applyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		applyButton = new JButton("Apply");
		applyButton.setPreferredSize(new Dimension(120, 28));
		applyButton.setBackground(new Color(60, 60, 60));
		applyButton.setForeground(Color.WHITE);
		applyButton.setFocusPainted(false);
		applyButton.addActionListener(e -> applyColor());
		applyPanel.add(applyButton);

		mainPanel.add(rgbPanel);
		mainPanel.add(Box.createVerticalStrut(15));
		mainPanel.add(applyPanel);

		add(mainPanel, BorderLayout.CENTER);

		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		applyTheme();
	}

	private void styleField(JTextField field)
	{
	    field.setBorder(BorderFactory.createCompoundBorder(
	        BorderFactory.createLineBorder(new Color(120,120,120)),
	        BorderFactory.createEmptyBorder(2,4,2,4)
	    ));
	}
	
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
	    try
	    {
	        int r = Integer.parseInt(redField.getText());
	        int g = Integer.parseInt(greenField.getText());
	        int b = Integer.parseInt(blueField.getText());

	        r = Math.max(0, Math.min(255, r));
	        g = Math.max(0, Math.min(255, g));
	        b = Math.max(0, Math.min(255, b));

	        Color color = new Color(r, g, b);
	        clocks[0].updateColor(color);

	    }
	    catch (NumberFormatException e)
	    {
	    	System.err.println(e);
	    }
	}
}