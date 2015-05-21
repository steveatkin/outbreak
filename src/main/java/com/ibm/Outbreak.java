package com.ibm;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.alchemy.Alert;

/**
 * Servlet implementation class Outbreak
 */
@WebListener
@WebServlet(name="Outbreak", urlPatterns = {"/Outbreak"}, loadOnStartup=1)
public class Outbreak extends HttpServlet implements ServletContextListener {
	private static final Logger logger = LoggerFactory.getLogger(Outbreak.class);
	private static final long serialVersionUID = 1L;
	private ScheduledExecutorService scheduler;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Outbreak() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void contextInitialized(ServletContextEvent event) {
    	logger.debug("Context intialized starting executor");
    	scheduler = Executors.newSingleThreadScheduledExecutor();
    	Runnable command = new FeedReader();
    	
    	long initialDelay = 1;
        TimeUnit unit = TimeUnit.MINUTES;
        // period the period between successive executions
        long period = 15;// 15 Minutes!
 
        scheduler.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    public void contextDestroyed(ServletContextEvent event) {
    	scheduler.shutdownNow();
    }
    
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		List<Alert> alerts = FeedReader.getAlerts();
		
		for(Alert alert: alerts) {
			System.out.println("ALERT: " + alert.toString());
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}