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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ConfigFrame extends JFrame
{

	private JTextField usernameField;
	private JCheckBox darkModeCheckBox;
	private JPanel mainPanel;
	private JPanel titleBar;
	private Color titleBarColor = new Color(47, 47, 47);
	private JButton saveButton;
	private JButton themeButton;
	private JButton closeButton;
	private Point dragOffset;
	private boolean darkMode = false;

	public ConfigFrame()
	{
		super("Configuration");

		// Window settings
		setUndecorated(true);
		setSize(400, 200);
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
		themeButton.setIcon(new ImageIcon(getClass().getResource("/pics/dark_mode.png")));

		themeButton.addActionListener(e -> {
			darkMode = !darkMode;
			if (darkMode)
				themeButton.setIcon(new ImageIcon(getClass().getResource("/pics/light_mode.png")));
			else
				themeButton.setIcon(new ImageIcon(getClass().getResource("/pics/dark_mode.png")));
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
		closeButton.setIcon(new ImageIcon(getClass().getResource("/pics/exit.png")));

		closeButton.addActionListener(e -> dispose());

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
		buttonPanel.setOpaque(true);
		buttonPanel.setBackground(titleBarColor);
		buttonPanel.add(themeButton);
		buttonPanel.add(closeButton);

		titleBar.add(buttonPanel, BorderLayout.EAST);

		add(titleBar, BorderLayout.NORTH);

		// Main panel
		mainPanel = new JPanel(new GridLayout(3, 2, 10, 10));

		mainPanel.add(new JLabel("Username:"));
		usernameField = new JTextField();
		mainPanel.add(usernameField);

		mainPanel.add(new JLabel("Dark Mode:"));
		darkModeCheckBox = new JCheckBox();
		mainPanel.add(darkModeCheckBox);

		saveButton = new JButton("Save");
		mainPanel.add(new JLabel());
		mainPanel.add(saveButton);

		saveButton.addActionListener(e -> saveConfig());

		add(mainPanel, BorderLayout.CENTER);

		applyTheme();
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

	private void saveConfig()
	{
		String username = usernameField.getText();
		boolean darkMode = darkModeCheckBox.isSelected();

		JOptionPane.showMessageDialog(this, "Saved!\nUsername: " + username + "\nDark Mode: " + darkMode);
	}
}