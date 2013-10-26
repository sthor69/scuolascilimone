package com.storassa.android.scuolasci;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kohsuke.github.GHIssueBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {
   private UncaughtExceptionHandler mDefaultExceptionHandler;

   // constructor
   public DefaultExceptionHandler(
         UncaughtExceptionHandler pDefaultExceptionHandler) {
      mDefaultExceptionHandler = pDefaultExceptionHandler;
   }

   @Override
   public void uncaughtException(final Thread t, final Throwable e) {

      // launch the thread to POST the server
      ExecutorService exec = Executors.newCachedThreadPool();
      exec.execute(new Runnable() {

         @Override
         public void run() {
            try {
//               GitHub hub = GitHub.connectUsingPassword("sthor69", "Gualano0,");
//               GHRepository repo = hub.getRepository("sthor69/scuolascilimone");
//               GHIssueBuilder builder = repo.createIssue("Application crasched");
//               builder.body(Convert.exceptionToString(e));
//               builder.create();
            } catch (Exception re) {
               re.printStackTrace();
               mDefaultExceptionHandler.uncaughtException(t, e);
            }

            // call original handler
            mDefaultExceptionHandler.uncaughtException(t, e);
         }
      });
   }

}
