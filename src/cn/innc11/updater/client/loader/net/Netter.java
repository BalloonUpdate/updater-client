package cn.innc11.updater.client.loader.net;

import cn.innc11.updater.client.loader.view.InfoWindow;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;

public class Netter extends NP
{
	private final static byte[] PROTOCOL_HEAD_ACK = {0x23, 0x04, 0x01, 0x34, 0x51, 0x33, 0x35, 0x18};
	
	public static final int NET_PROTOCOL_VERSION = 1;
	
	private String host;
	private int port;
	private Socket socket;
	
	private File jarFile;
	private String mainClass;
	
	private InfoWindow window;//用于回调，用来设置下载进度信息

	public Netter(String host, int port, InfoWindow window)
	{
		this.host = host;
		this.port = port;
		this.window = window;
	}
	
	public String getHost()
	{
		return host+":"+port;
	}
	
	public File getJarFile()
	{
		return jarFile;
	}
	
	public String getMainClass()
	{
		return mainClass;
	}


	public void start() throws UnknownHostException, IOException
	{
		//设置状态提示文本
		window.setStateText("正在连接到服务器。。。("+getHost()+")");
		
		//发起连接
		try{
			socket = new Socket(host, port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			window.destory();
			JOptionPane.showMessageDialog(null, "无法连接到: "+getHost(), "连接失败！", 0);
			Runtime.getRuntime().exit(0);
		}
		
		//设置IO超时
		window.setStateText("正在设置IO超时。。。");
		socket.setSoTimeout(40000);
		
		
		//打开IO流
		window.setStateText("正在打开IO流。。。");
		netIn = new DataInputStream(socket.getInputStream());
		netOut = new DataOutputStream(socket.getOutputStream());
	
		
		//测试协议
		window.setStateText("正在测试传输协议。。。");
		
		if(!Ack(PROTOCOL_HEAD_ACK))
		{
			window.destory();
			JOptionPane.showMessageDialog(null, "协议测试未通过，请检查端口是否被占用或者设置正确！", "协议错误", 0);
			Runtime.getRuntime().exit(0);
		}
		
		//告诉服务端客户端的协议版本
		writeInt(NET_PROTOCOL_VERSION);
		
		//如果协议版本服务端无法处理
		if(!readBoolean())
		{
			//读取服务端使用的协议版本
            String serverSNPVer = readString();
            
            window.destory();
			JOptionPane.showMessageDialog(null, "协议版本不支持，当前的协议版本为 "+NET_PROTOCOL_VERSION+"\n支持的版本为"+serverSNPVer, "协议版本不支持", 0);
//			JOptionPane.showMessageDialog(null, msg0, msg1, 0);
			Runtime.getRuntime().exit(0);
		}
		
		window.setStateText("正在传回内容。。。");
		mainClass = readString();
		
		//接收文件长度
		long fileLength = netIn.readLong();
		jarFile = File.createTempFile("updater_client_core", ".jar");
		//jarFile = new File("aaaaaaa.jar");

		jarFile.createNewFile();
		
		FileOutputStream fos = new FileOutputStream(jarFile);
	
		byte[] buf = new byte[1024];
		int ac = (int)(fileLength / buf.length);
		int bc = (int)(fileLength % buf.length);

		for (int c = 0; c < ac; c++)
		{
			netIn.readFully(buf);
			fos.write(buf, 0, buf.length);
			int progress = (int)( ((float)c / (float)ac)*100);
			window.setStateText("正在传回内容。。。"+progress);
		}

		for (int c = 0; c < bc; c++)
		{
			fos.write(netIn.readByte());
		}


		fos.close();
	}
	
	
	public Socket getSocket()
	{
		return socket;
	}

}
