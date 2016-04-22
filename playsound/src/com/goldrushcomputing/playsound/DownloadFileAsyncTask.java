package com.goldrushcomputing.playsound;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class DownloadFileAsyncTask extends AsyncTask<String, Void, Boolean>
{
	final int BUFFER_SIZE = 1024;
	final String TAG = "DownloadFileAsyncTask";
	
	@Override
	protected Boolean doInBackground(String... params)
	{
		HttpClient client = new DefaultHttpClient();
		
		/*
		 CredentialsProvider credProvider = new BasicCredentialsProvider();
		    credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
		        new UsernamePasswordCredentials("client", "mzr"));
		    	   
		    ((DefaultHttpClient)client).setCredentialsProvider(credProvider);
		    */
		    
        HttpGet get = new HttpGet(params[0]);
        
        try
        {
            HttpResponse response = client.execute(get);
            
            StatusLine statusLine = response.getStatusLine();
            
            if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK)
            {
            	InputStream is = null;
            	BufferedInputStream bis = null;
				BufferedOutputStream bos = null;
            	
            	try
            	{
            		is = response.getEntity().getContent();
            		
            		File file = new File(params[1]);
            		
            		Log.d(TAG, "file path = " + file.getPath());

					file.createNewFile();

					bis = new BufferedInputStream(is, BUFFER_SIZE);
					bos = new BufferedOutputStream(new FileOutputStream(file, false), BUFFER_SIZE);

					byte buffer[] = new byte[BUFFER_SIZE];
					int size = 0;

					while (-1 != (size = bis.read(buffer)))
					{
						bos.write(buffer, 0, size);
					}

					bos.flush();
            	}
            	finally
            	{
            		if (bos != null)
            		{
						bos.close();
					}

					if (bis != null)
					{
						bis.close();
					}
					
            		if (is != null)
            		{
            			is.close();
            		}
            	}
            }
            
            return Boolean.TRUE;
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        	return Boolean.FALSE;
        }
        finally
        {
        	client.getConnectionManager().shutdown(); 
        }
	}
}