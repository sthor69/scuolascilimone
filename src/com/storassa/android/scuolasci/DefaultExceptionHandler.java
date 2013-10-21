package com.storassa.android.scuolasci;
import java.lang.Thread.UncaughtExceptionHandler;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;


public class DefaultExceptionHandler implements UncaughtExceptionHandler {
   private UncaughtExceptionHandler mDefaultExceptionHandler;

   //constructor
   public DefaultExceptionHandler(UncaughtExceptionHandler pDefaultExceptionHandler)
   {
        mDefaultExceptionHandler= pDefaultExceptionHandler;
   }
   
   @Override
   public void uncaughtException(Thread t, Throwable e) {
      try {
      GitHub hub = GitHub.connectUsingPassword("sthor69", "Gualano0,");
      GHRepository repo = hub.getRepository("sthor69/scuolascilimone");
      GHIssueBuilder builder = repo.createIssue("Title test");
      builder.body(Convert.exceptionToString(e));
      GHIssue issue = builder.create(); 
      } catch (Exception re) {
         re.printStackTrace();
      }

      //call original handler  
      mDefaultExceptionHandler.uncaughtException(t, e);        

}

}
