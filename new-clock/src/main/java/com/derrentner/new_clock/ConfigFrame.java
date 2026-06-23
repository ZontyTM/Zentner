package com.derrentner.new_clock;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class ConfigFrame extends JFrame
{
	private JPanel titleBar;
	private JButton themeButton;
	private JButton closeButton;
	private Point dragOffset;

	private JPanel mainPanel;

	private JLabel previewLabel;

	private JCheckBox secondsCheckBox;
	
	private JTextField sizeField;
	private JButton increaseButton;
	private JButton decreaseButton;
	
	private JLabel positionLabel;
	private JButton leftPositionButton;
	private JButton rightPositionButton;
	private Main.DisplayPosition selectedPosition = Main.DisplayPosition.TopRight;
	
	private JComboBox<String> monitorBox;

	private JLabel bgTransparencyLabel;
	private JSlider bgTransparency;

	private JTabbedPane tabs;
	private JSlider redSlider, greenSlider, blueSlider;
	private JSlider hueSlider, satSlider, valSlider;

	private JButton applyButton;

//	private ClockFrame[] clocks;
	private Color titleBarColor = new Color(47, 47, 47);
	private boolean darkMode = true;
	String[] monitorNames;
	
	private Settings settings;
	private List<ClockFrame> clockFrames;

    public ConfigFrame(Settings settings, List<ClockFrame> clockFrames)
    {
        this.settings = settings;
        this.clockFrames = clockFrames; // kinda doubled

		GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		monitorNames = new String[screens.length];

		for (int i = 0; i < screens.length; i++)
		{
		    DisplayMode dm = screens[i].getDisplayMode();

		    monitorNames[i] = "Monitor " + (i + 1) + " (" + dm.getWidth() + "x" + dm.getHeight() + ")";
		}
		
		// Window settings
		setUndecorated(true);
		setSize(400, 500);
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
		Color color = settings.getClocks().getFirst().getColor();

		// ===== PREVIEW =====
		JPanel previewPanel = new JPanel(new BorderLayout());
		previewPanel.setBackground(Color.BLACK);
		previewPanel.setPreferredSize(new Dimension(200, 60));

		previewLabel = new JLabel(settings.getClocks().getFirst().isShowSeconds() ? "12:34:56" : "12:34", JLabel.CENTER);
		previewLabel.setFont(previewLabel.getFont().deriveFont(28f));
		previewPanel.add(previewLabel, BorderLayout.CENTER);

		// ===== SECONDS CHECKBOX =====
		secondsCheckBox = new JCheckBox("Show Seconds");
		secondsCheckBox.setFocusPainted(false);
		secondsCheckBox.setSelected(settings.getClocks().getFirst().isShowSeconds());
		secondsCheckBox.addActionListener(e ->
		{
		    if (secondsCheckBox.isSelected())
		    {
		        previewLabel.setText("12:34:56");
		    }
		    else
		    {
		        previewLabel.setText("12:34");
		    }
		});
		
		// ===== TextSize ======
		decreaseButton = new JButton("-");
		sizeField = new JTextField(String.valueOf(settings.getClocks().getFirst().getTextSize()), 3); // default size
		increaseButton = new JButton("+");

		Dimension btnSize = new Dimension(45, 25);
		decreaseButton.setPreferredSize(btnSize);
		decreaseButton.setFocusable(false);
		increaseButton.setPreferredSize(btnSize);
		increaseButton.setFocusable(false);
		
		decreaseButton.addActionListener(e -> {
		    try {
		        int size = Integer.parseInt(sizeField.getText());

		        if (size > 1) {
		            size--;
		            sizeField.setText(String.valueOf(size));
		        }
		    }
		    catch (NumberFormatException ignored) {}
		});

		increaseButton.addActionListener(e -> {
		    try {
		        int size = Integer.parseInt(sizeField.getText());

		        size++;
		        sizeField.setText(String.valueOf(size));
		    }
		    catch (NumberFormatException ignored) {}
		});
		
		// ==== Display Position ====
		positionLabel = new JLabel((settings.getClocks().getFirst().getMonitorPosition() == Main.DisplayPosition.TopLeft) ? "Top Left" : "Top Right");

		leftPositionButton = new JButton("<");
		rightPositionButton = new JButton(">");

		Dimension arrowSize = new Dimension(45, 25);
		leftPositionButton.setPreferredSize(arrowSize);
		rightPositionButton.setPreferredSize(arrowSize);
		
		leftPositionButton.setFocusable(false);
		rightPositionButton.setFocusable(false);
		
		leftPositionButton.addActionListener(e -> togglePosition());
		rightPositionButton.addActionListener(e -> togglePosition());
		
		selectedPosition = clockFrames.getFirst().getDisplayPosition();
		positionLabel.setText( (selectedPosition == Main.DisplayPosition.TopLeft) ? "Top Left" : "Top Right");
		
		// ==== Display ====
		monitorBox = new JComboBox<>(monitorNames);
		monitorBox.setPreferredSize(new Dimension(100, 25));
		monitorBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
		monitorBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		monitorBox.setSelectedIndex(clockFrames.getFirst().getDisplay());
		
		// ==== BG Transparency ====
		bgTransparencyLabel = new JLabel("Background Transparency");
		bgTransparency = new JSlider(0, 100);
		bgTransparency.setValue(settings.getClocks().getFirst().getBGTransparency());
		bgTransparency.setMajorTickSpacing(50);
		bgTransparency.setPaintTicks(true);
		bgTransparency.setPreferredSize(new Dimension(150, 50));
		bgTransparency.setMaximumSize(new Dimension(150, 50));
		
		// ==== Preview and Settings Wrapper =====
		JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

		controlsPanel.add(decreaseButton);
		controlsPanel.add(sizeField);
		controlsPanel.add(increaseButton);

		controlsPanel.add(Box.createHorizontalStrut(25));

		controlsPanel.add(leftPositionButton);
		controlsPanel.add(positionLabel);
		controlsPanel.add(rightPositionButton);
		

		JPanel settingsWrapper = new JPanel();
		settingsWrapper.setLayout(new BoxLayout(settingsWrapper, BoxLayout.Y_AXIS));

		previewPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		secondsCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		bgTransparencyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		settingsWrapper.add(previewPanel);
		settingsWrapper.add(secondsCheckBox);
		settingsWrapper.add(Box.createVerticalStrut(5));
		settingsWrapper.add(controlsPanel);
		settingsWrapper.add(Box.createVerticalStrut(5));
		settingsWrapper.add(monitorBox);
		settingsWrapper.add(Box.createVerticalStrut(15));
		settingsWrapper.add(bgTransparencyLabel);
		settingsWrapper.add(bgTransparency);
		
		
		// ===== TABS =====
		tabs = new JTabbedPane();

		UIManager.put("TabbedPane.background", new Color(40,40,40));
		UIManager.put("TabbedPane.foreground", Color.WHITE);
		UIManager.put("TabbedPane.selected", new Color(70,70,70));
		UIManager.put("TabbedPane.contentAreaColor", new Color(30,30,30));
		UIManager.put("TabbedPane.borderHightlightColor", new Color(60,60,60));
		UIManager.put("TabbedPane.shadow", new Color(20,20,20));
		
		// RGB TAB
		JPanel rgbPanel = new JPanel();
		rgbPanel.setLayout(new BoxLayout(rgbPanel, BoxLayout.Y_AXIS));

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

		redSlider.setPreferredSize(new Dimension(150, 50));
		redSlider.setMaximumSize(new Dimension(150, 50));
		greenSlider.setPreferredSize(new Dimension(150, 50));
		greenSlider.setMaximumSize(new Dimension(150, 50));
		blueSlider.setPreferredSize(new Dimension(150, 50));
		blueSlider.setMaximumSize(new Dimension(150, 50));
		
		
		tabs.addTab("RGB", rgbPanel);

		// HSV TAB
		JPanel hsvPanel = new JPanel(new GridLayout(3, 1));
		hsvPanel.setLayout(new BoxLayout(hsvPanel, BoxLayout.Y_AXIS));

		float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

		hueSlider = new JSlider(0, 360, (int)(hsv[0] * 360));
		satSlider = new JSlider(0, 100, (int)(hsv[1] * 100));
		valSlider = new JSlider(0, 100, (int)(hsv[2] * 100));

		ChangeListener hsvListener = e -> updatePreview();

		hueSlider.addChangeListener(hsvListener);
		hueSlider.setMajorTickSpacing(90);
		hueSlider.setPaintTicks(true);
		hueSlider.setPaintLabels(true);
		hueSlider.setPreferredSize(new Dimension(150, 50));
		hueSlider.setMaximumSize(new Dimension(150, 50));
		
		satSlider.addChangeListener(hsvListener);
		satSlider.setMajorTickSpacing(25);
		satSlider.setPaintTicks(true);
		satSlider.setPaintLabels(true);
		satSlider.setPreferredSize(new Dimension(150, 50));
		satSlider.setMaximumSize(new Dimension(150, 50));
		
		valSlider.addChangeListener(hsvListener);
		valSlider.setMajorTickSpacing(25);
		valSlider.setPaintTicks(true);
		valSlider.setPaintLabels(true);
		valSlider.setPreferredSize(new Dimension(150, 50));
		valSlider.setMaximumSize(new Dimension(150, 50));

		hsvPanel.add(labeledSlider("H", hueSlider));
		hsvPanel.add(labeledSlider("S", satSlider));
		hsvPanel.add(labeledSlider("V", valSlider));

		tabs.addTab("HSV", hsvPanel);
		
		updateTabColors();
		tabs.addChangeListener(e -> updateTabColors());
		
		// =====     WRAP      =====
		JPanel center = new JPanel(new BorderLayout(10, 10));

		center.add(settingsWrapper, BorderLayout.NORTH);
		center.add(tabs, BorderLayout.CENTER);

		mainPanel.add(center, BorderLayout.CENTER);

		// Apply Button
		JPanel applyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		applyButton = new JButton("Apply");
		applyButton.setPreferredSize(new Dimension(120, 28));
		applyButton.setBackground(new Color(60, 60, 60));
		applyButton.setForeground(Color.WHITE);
		applyButton.setFocusPainted(false);
		applyButton.addActionListener(e ->
		{
		    applyColor();
		    applyType();
		    applyTextSize();
		    applyPosition();
		    applyMonitor();
		    applyBGTransparency();
		    clockFrames.getFirst().loadImages();
		    SettingsManager.save(settings);
		});

		applyPanel.add(applyButton);

		mainPanel.add(applyPanel, BorderLayout.SOUTH);

		// Rest
		add(mainPanel, BorderLayout.CENTER);
		applyTheme();
	}
	
	private void togglePosition()
	{
	    if (selectedPosition == Main.DisplayPosition.TopRight)
	    {
	        selectedPosition = Main.DisplayPosition.TopLeft;
	        positionLabel.setText("Top Left");
	    }
	    else
	    {
	        selectedPosition = Main.DisplayPosition.TopRight;
	        positionLabel.setText("Top Right");
	    }
	}
	
	private void updateTabColors()
	{
		if (tabs.getSelectedIndex() == 0)
		{
			Color rgb = Color.getHSBColor(hueSlider.getValue()/360f, satSlider.getValue()/100f, valSlider.getValue()/100f);
			
			redSlider.setValue(rgb.getRed());
			greenSlider.setValue(rgb.getGreen());
			blueSlider.setValue(rgb.getBlue());	
		}
		else
		{
			float[] hsv = Color.RGBtoHSB(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue(), null);

			hueSlider.setValue((int)(hsv[0] * 360));
			satSlider.setValue((int)(hsv[1] * 100));
			valSlider.setValue((int)(hsv[2] * 100));
		}

		

	    for (int i = 0; i < tabs.getTabCount(); i++)
	    {
	        if (i == tabs.getSelectedIndex())
	        {
	            tabs.setForegroundAt(i, Color.BLACK);
	        }
	        else
	        {
	            tabs.setForegroundAt(i, Color.WHITE);
	        }
	    }
	}
	
	private JSlider createSlider(int value)
	{
	    JSlider slider = new JSlider(0, 255, value);
	    slider.setMajorTickSpacing(85);
	    slider.setPaintTicks(true);
	    slider.setPaintLabels(true);
	    return slider;
	}
	
	public void updatePreview()
	{
	    Color c = getCurrentColor();

	    previewLabel.setForeground(c);

	    // optional but HIGHLY recommended: contrast fix
	    float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
	    previewLabel.setOpaque(true);
	    previewLabel.setBackground(hsb[2] < 0.5 ? Color.WHITE : Color.BLACK);
	}
	
	private JPanel labeledSlider(String label, JSlider slider)
	{
	    JPanel panel = new JPanel(new BorderLayout());

	    panel.setBorder(
	        BorderFactory.createEmptyBorder(0, 20, 0, 20)
	    );

	    panel.add(new JLabel(label), BorderLayout.WEST);
	    panel.add(slider, BorderLayout.CENTER);

	    return panel;
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

		updatePreview();
		repaint();
	}

	private void applyType()
	{
		if(secondsCheckBox.isSelected()) clockFrames.getFirst().setType(ClockFrame.ClockType.HourMinuteSecond);
		else clockFrames.getFirst().setType(ClockFrame.ClockType.HourMinute);
		settings.getClocks().getFirst().setShowSeconds(secondsCheckBox.isSelected());
	}
	
	private void applyColor()
	{
	    Color color = getCurrentColor();
	    settings.getClocks().getFirst().setColor(color);
	    clockFrames.getFirst().updateColor(color);
	}
	
	private void applyTextSize()
	{
	    try
	    {
	        int size = Integer.parseInt(sizeField.getText());
	        if (size < 1) size = 1;
	        settings.getClocks().getFirst().setTextSize(size);
	        clockFrames.getFirst().changeTextSize(size);
	    }
	    catch (NumberFormatException ex)
	    {
	        JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Invalid Size", JOptionPane.ERROR_MESSAGE);
	    }
	}
	
	private void applyPosition()
	{
		clockFrames.getFirst().updatePosition(selectedPosition);
		settings.getClocks().getFirst().setMonitorPosition(selectedPosition);
	}

	private void applyMonitor()
	{
		clockFrames.getFirst().updateDisplay(monitorBox.getSelectedIndex());
		settings.getClocks().getFirst().setMonitor(monitorBox.getSelectedIndex());
	}
	
	private void applyBGTransparency()
	{
		clockFrames.getFirst().changeBGTransparency(bgTransparency.getValue());
		settings.getClocks().getFirst().setBGTransparency(bgTransparency.getValue());
	}
}