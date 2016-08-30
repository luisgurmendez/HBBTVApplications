<%@page import="javax.swing.SwingUtilities"%>
<%@page import="java.awt.Component"%>
<%@page import="java.awt.event.WindowEvent"%>
<%@page import="java.awt.event.ActionEvent"%>
<%@page import="javax.swing.AbstractAction"%>
<%@page import="javax.swing.KeyStroke"%>
<%@page import="uy.edu.um.managers.TimeManager" %>

<%@page import="java.awt.event.KeyEvent"%>
<%@page import="javax.swing.JComponent"%>
<%@page import="javax.swing.WindowConstants"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page
	import="javax.swing.JFrame,com.sun.jna.NativeLibrary,uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent,uk.co.caprica.vlcj.runtime.RuntimeUtil"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Video Player</title>
</head>
<%
	long startTime = TimeManager.getInstance(true).getStartTime();
	long currentTime = System.nanoTime();
	long startTimeInMilliseconds = (long)((currentTime - startTime) / 1000000);
%>
<body onload="initializaVideoSettings(<%= startTimeInMilliseconds %>);">
	<embed type="application/x-vlc-plugin" loop="yes" id="vlc" autostart="yes" target="http://localhost:8080/RealTimeSummary/FriendsEpisode/itsNotACat" />
	<!-- http://dev.hbbtvhat.com/index.php/canal-4/ -->
	<div id="demo"></div>
	<script type="text/javascript">
		function initializaVideoSettings(startTime){
			var vlc = document.getElementById("vlc");
			vlc.video.fullscreen = true;
			vlc.input.time = startTime;
		}
	</script>

	<%
		/*JFrame ourFrame = new JFrame();
		EmbeddedMediaPlayerComponent ourMediaPlayer;
		String vlcPath = "C:\\Program Files\\VideoLAN\\VLC";
		String mediaPath = "C:\\Users\\tedym\\Desktop\\Workspaces\\Resources\\suarez.mp4";
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),
				vlcPath);
		ourMediaPlayer = new EmbeddedMediaPlayerComponent();
		ourFrame.setContentPane(ourMediaPlayer);
		ourFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		ourFrame.setUndecorated(true);
		ourFrame.setVisible(true);
		ourMediaPlayer.getMediaPlayer().playMedia(mediaPath);
		ourFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		ourFrame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"Cancel");
		ourFrame.getRootPane().getActionMap().put("Cancel", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				 Component component = (Component) e.getSource();
			     JFrame frame = (JFrame) SwingUtilities.getRoot(component);
			     frame.setVisible(false);
			     frame.dispose();
			}
		}); */
	%>
</body>
</html>