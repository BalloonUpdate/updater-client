package cn.innc11.updater.client.loader;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class InfoWindow
{
	private JFrame window;
	private Container rootPanel;
		private JLabel state;

	public static void main(String[] ag) throws IOException
	{
		InfoWindow w = new InfoWindow();
	}
	
	public InfoWindow()
	{
		window = new JFrame();
		rootPanel = window.getContentPane();
	
		window.setTitle("OS: " + System.getProperty("os.name"));
		window.setUndecorated(false);
		window.setAlwaysOnTop(false);
		window.setSize(350, 200);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		state = new JLabel("正在初始化");
		state.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		state.setAlignmentY(JLabel.CENTER_ALIGNMENT);
		rootPanel.setLayout(new BorderLayout());
		rootPanel.add(state, BorderLayout.CENTER);
		
		window.setVisible(true);
	}
	

	// UI 的 API
	public void setStateText(String str)
	{
		state.setText(str);
	}
	
	public void setWindowTitleText(String str)
	{
		window.setTitle(str);
	}
	
	public void destory()
	{
		window.dispose();
	}
	
	
	public void bukeshi()
	{
		window.setVisible(false);
	}
	
}
