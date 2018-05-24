package practicaltest02.pdsd.systems.cs.pub.ro.practicaltest02;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by student on 24.05.2018.
 */

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket clientSocket;

    public CommunicationThread(ServerThread serverThread, Socket clientSocket) {
        this.serverThread = serverThread;
        this.clientSocket = clientSocket;
    }

    public void run() {
        if (clientSocket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(clientSocket);
            PrintWriter printWriter = Utilities.getWriter(clientSocket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (word)");
            String word = bufferedReader.readLine();
            if (word == null || word.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (word");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://services.aonaware.com/DictService/DictService.asmx/Define");
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("word", word));
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            httpPost.setEntity(urlEncodedFormEntity);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            String pageSourceCode = httpClient.execute(httpPost, responseHandler);
            if (pageSourceCode == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                return;
            }

            Log.i("DEFINITION",pageSourceCode);
            int indexStartDef = pageSourceCode.indexOf("<WordDefinition>");
            int indexStartOfDef = indexStartDef + "<WordDefinition>".length();
            int indexEndOfDef = pageSourceCode.indexOf("</WordDefinition>");
            String result = pageSourceCode.substring(indexStartOfDef, indexEndOfDef);

            if (TextUtils.isEmpty(result)) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Definition is null!");
                return;
            }
            printWriter.println(result);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }
}
