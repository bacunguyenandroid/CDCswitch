package com.bacunguyenandroid.cdcswitchclient.Net;import android.util.Log;import com.google.gson.Gson;import java.io.IOException;import java.nio.charset.StandardCharsets;import java.util.Arrays;import java.net.InetAddress;import java.net.ServerSocket;import java.net.Socket;import java.util.ArrayList;import java.util.List;import java.util.concurrent.atomic.AtomicBoolean;public class Socketworker {    // TODO : biến    private List<SocketworkerListener> socketworkerListeners = new ArrayList<SocketworkerListener>();    private ConnectPc connectPc;    private SendToServer sendToServer;    //Add listener;    public  void AddConnectListener(SocketworkerListener socketworkerListener){        socketworkerListeners.add(socketworkerListener);    }    private  void CallConnected(){        for (SocketworkerListener skln : socketworkerListeners){            skln.OnTalkStart();        }    }    private  void CallDisconnected(){        for (SocketworkerListener skln : socketworkerListeners){            skln.OnTalkStop();        }    }    private  void CallTakeData(Talkcontent talkcontent){        for (SocketworkerListener skln : socketworkerListeners){            skln.OnDataTake(talkcontent);        }    }    public  void ConnectToPc(){           connectPc = new ConnectPc();           connectPc.StartConnect();    }    public  void StopConnectPc(){           connectPc.StopConnect();    }    public void GiveDatatoPc(Talkcontent talkcontent){           sendToServer  = new SendToServer();           sendToServer.Send(talkcontent);    }    private  void SendData(Talkcontent talkcontent) throws IOException {        Gson gson = new Gson();        String json = gson.toJson(talkcontent);        byte[] data = json.getBytes();        Neter.Datatranfer.getOutputStream().write(data);        Neter.Datatranfer.getOutputStream().flush();    }    private  Talkcontent Readdata() throws IOException {        Gson gson = new Gson();        byte[] data  = new byte[1024];        int len = Neter.Datatranfer.getInputStream().read(data);        byte[] subdata = Arrays.copyOfRange(data,0,len); //đếu hiểu 14-1=12 ???        String json = new String(subdata, StandardCharsets.UTF_8);        Talkcontent talkcontent = gson.fromJson(json,Talkcontent.class);        return  talkcontent;    }    // TODO:Custom thread class    private  class  BinđingServer implements Runnable {        // TODO: BIẾN        public  boolean IsConnected;        private  int Port;        private  Thread myThread;        private ServerSocket serverSocket;        private  BinđingServer(int Port){            this.Port = Port;            IsConnected = false;        }        @Override        public void run() {            try {                Log.i("SOCKET","socket server start listen at "+Port);                serverSocket =  new ServerSocket(Port,1, InetAddress.getByName("127.0.0.1"));                Socket socket = serverSocket.accept();                if(Port == Neter.MOUSEUSER_PORT) Neter.Mouseuser = socket;                if(Port == Neter.DATATRANFER_PORT) Neter.Datatranfer = socket;                IsConnected = true;            } catch (IOException e) {            }        }        public  void Stop(){            try {                Log.i("SOCKET","socket server stop listen at "+Port);                serverSocket.close();            } catch (IOException e) {            }        }        public  void Start(){            myThread = new Thread(this);            myThread.start();        }    }    private  class ConnectPc extends  Thread{        // TODO : biến        private final AtomicBoolean Iswantstop = new AtomicBoolean(false);        private BinđingServer DatatranferServer;        private BinđingServer MouseuserServer;        private TakefromServer TakefromServer;        @Override        public void run() {            DatatranferServer = new BinđingServer(Neter.DATATRANFER_PORT);            MouseuserServer = new BinđingServer(Neter.MOUSEUSER_PORT);            DatatranferServer.Start();            MouseuserServer.Start();            while (!Iswantstop.get()){                if(DatatranferServer.IsConnected && MouseuserServer.IsConnected){                    // TODO : fist talk                    FirstTalk();                    //TODO : hear data                    TakefromServer = new TakefromServer();                    TakefromServer.StartHearData();;                    // TODO : talk together                    TalkTogether();                    break;                }            }        }        public void StartConnect(){            Iswantstop.set(false);            this.start();        }        private void TalkTogether(){            try {                while (true){                    Thread.sleep(800);                    //gửi lên server pc                    Talkcontent talkcontent = new Talkcontent();                    talkcontent.TalkCode = TalkCode.JUSTTALK;                    SendData(talkcontent);                }            }            catch (IOException e){                //khi gửi file lên server thất bại thì nó sẽ lỗi và call talk stop                CallDisconnected();            }            catch (InterruptedException e){                System.exit(0);            }        }        // TODO :  talk together        private  void FirstTalk() {            try {                Talkcontent talkcontent  = new Talkcontent();                talkcontent.TalkCode = TalkCode.JUSTTALK;                SendData(talkcontent);                Log.i("SOCKET","sent first message");                // TODO: Notify                CallConnected();            }            catch (IOException e){            }        }        public void StopConnect(){            //stop hear data            TakefromServer.StopHearData();            //Stop listen at datatranfer            DatatranferServer.Stop();            MouseuserServer.Stop();            //stop check server connect            Iswantstop.set(true);            //        }    }    private  class SendToServer implements Runnable{        private  Talkcontent talkcontent;        @Override        public void run() {            try {                SendData(talkcontent);                Log.i("SOCKET","sent a package to PC done !");            } catch(IOException e){                Log.i("SOCKET","error sent a package to PC !");            }        }        public  void  Send(Talkcontent talkcontent){            this.talkcontent = talkcontent;            Thread thread = new Thread(this);            thread.start();        }    }    private  class  TakefromServer extends  Thread{        private final AtomicBoolean Iswantstop = new AtomicBoolean(false);        @Override        public void run() {            super.run();        }        public void StartHearData(){            Iswantstop.set(false);            while (!Iswantstop.get()){                try {                    Talkcontent talkcontent = Readdata();                    CallTakeData(talkcontent);                } catch (IOException e) {                    break;                }            }        }        public  void StopHearData(){           Iswantstop.set(true);        }    }}