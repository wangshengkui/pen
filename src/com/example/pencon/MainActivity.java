package com.example.pencon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;



import com.example.pencon12.R;
import com.google.common.collect.ArrayListMultimap;
import com.tqltech.tqlpencomm.Dot;
import com.tqltech.tqlpencomm.PenCommAgent;
import com.tqltech.tqlpencomm.util.BLEFileUtil;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi") public class MainActivity extends Activity {
	
    private boolean bIsReply = false;	
    private ImageView gImageView;
	private RelativeLayout gLayout;
    private DrawView[] bDrawl = new DrawView[2];  //add 2016-06-15 for draw
    private final static String TAG = "OidActivity";
    private final static boolean isSaveLog = false;          //是否保存绘制数据到日志
    private final static String LOGPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TQL/"; //绘制数据保存目录

//    private BluetoothLEService mService = null;              //蓝牙服务
 
    private static final int REQUEST_SELECT_DEVICE = 1;      //蓝牙扫描
    private static final int REQUEST_ENABLE_BT = 2;          //开启蓝牙
    private static final int REQUEST_LOCATION_CODE = 100;    //请求位置权限
    private static final int GET_FILEPATH_SUCCESS_CODE = 1000;//获取txt文档路径成功

    private int penType = 1;                                 //笔类型（0：TQL-101  1：TQL-111  2：TQL-112 3: TQL-101A）

    private double XDIST_PERUNIT = Constants.XDIST_PERUNIT;  //码点宽
    private double YDIST_PERUNIT = Constants.YDIST_PERUNIT;  //码点高
    private double A5_WIDTH = Constants.A5_WIDTH;            //本子宽
    private double A5_HEIGHT = Constants.A5_HEIGHT;          //本子高
	public double A4_WIDTH = Constants.A4_WIDTH; // 本子宽
	public double A4_HEIGHT = Constants.A4_HEIGHT; // 本子高
	
	
    private double  A5_BG_REAL_WIDTH = Constants.A5_BG_REAL_WIDTH;     //资源背景图宽
    private double A5_BG_REAL_HEIGHT = Constants.A5_BG_REAL_HEIGHT;   //资源背景图高
    private double A4_BG_REAL_WIDTH = Constants.A4_BG_REAL_WIDTH;     //资源背景图宽
    private double A4_BG_REAL_HEIGHT = Constants.A4_BG_REAL_HEIGHT;   //资源背景图高
    private double A0_BG_REAL_WIDTH = Constants.A0_BG_REAL_WIDTH;     //资源背景图宽
    private double A0_BG_REAL_HEIGHT = Constants.A0_BG_REAL_HEIGHT;   //资源背景图高
    
    private int BG_WIDTH;                                    //显示背景图宽
    private int BG_HEIGHT;                                   //显示背景图高
    private int A5_X_OFFSET;                                 //笔迹X轴偏移量
    private int A5_Y_OFFSET;                                 //笔迹Y轴偏移量
    private int gcontentLeft;                                //内容显示区域left坐标
    private int gcontentTop;                                 //内容显示区域top坐标

    public static float mWidth;                              //屏幕宽
    public static float mHeight;                             //屏幕高

    private float mov_x;                                     //声明起点坐标
    private float mov_y;                                     //声明起点坐标
    private int gCurPageID = -1;                             //当前PageID
    private int gCurBookID = -1;                             //当前BookID
    private float gScale = 1;                                //笔迹缩放比例
    private int gColor = 6;                                  //笔迹颜色
    private int gWidth = 3;                                  //笔迹粗细
    private int gSpeed = 30;                                 //笔迹回放速度
    private float gOffsetX = 0;                              //笔迹x偏移
    private float gOffsetY = 0;                              //笔迹y偏移

    private ArrayListMultimap<Integer, Dots> dot_number = ArrayListMultimap.create();  //Book=100笔迹数据
    private ArrayListMultimap<Integer, Dots> dot_number1 = ArrayListMultimap.create(); //Book=0笔迹数据
    private ArrayListMultimap<Integer, Dots> dot_number2 = ArrayListMultimap.create(); //Book=1笔迹数据
    private ArrayListMultimap<Integer, Dots> dot_number4 = ArrayListMultimap.create(); //笔迹回放数据
    private Intent serverIntent = null;
    private Intent LogIntent = null;
    private PenCommAgent bleManager;
    private String penAddress;

    public static float g_x0, g_x1, g_x2, g_x3;
    public static float g_y0, g_y1, g_y2, g_y3;
    public static float g_p0, g_p1, g_p2, g_p3;
    public static float g_vx01, g_vy01, g_n_x0, g_n_y0;
    public static float g_vx21, g_vy21;
    public static float g_norm;
    public static float g_n_x2, g_n_y2;

    private int gPIndex = -1;
    private boolean gbSetNormal = false;
    private boolean gbCover = false;

    private float pointX;
    private float pointY;
    private int pointZ;

    private boolean bIsOfficeLine = false;
//    private RoundProgressBar bar;
	private BluetoothLEService mService = null;              //蓝牙服务	
	
    private RelativeLayout dialog;
    private Button confirmBtn;
    private TextView showInftTextView;



    private float gpointX;
    private float gpointY;
	
    private String gStrHH = "";
    private boolean bLogStart = false;
    
    public int mN;
    //wsk 2019.3.21
    public SQLiteDatabase database;
    
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(final ComponentName className, IBinder rawBinder) {
            mService = ((BluetoothLEService.LocalBinder) rawBinder).getService();
            Log.d("mService:", "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                finish();
            }

            mService.setOnDataReceiveListener(new BluetoothLEService.OnDataReceiveListener() {
                @Override
                public void onDataReceive(final Dot dot) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          Log.i("zgm","Dot信息,BookID"+dot.BookID);
                          Log.i("zgm","Dot信息,ab_x"+dot.ab_x);
                            ProcessDots(dot);
                        }
                    });
                }

                @Override
                public void onOfflineDataReceive(final Dot dot) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           ProcessDots(dot);
                        }
                    });
                }

                @Override
                public void onFinishedOfflineDown(boolean success) {
                    //Log.i(TAG, "---------onFinishedOfflineDown--------" + success);
/*                	
                    layout.setVisibility(View.GONE);
                    bar.setProgress(0);
*/
                }

                @Override
                public void onOfflineDataNum(final int num) {
                    //Log.i(TAG, "---------onOfflineDataNum1--------" + num);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                	textView.setText("离线数量有" + Integer.toString(num * 10) + "bytes");
 /*                              
                               	
                                    //if (num == 0) {
                                    //    return;
                                    //}

                                	Log.e("zgm","R.id.dialog1"+R.id.dialog);
                                    dialog = (RelativeLayout)findViewById(R.id.dialog);
                                    Log.e("zgm","dialog"+dialog.getId());
                                    dialog.setVisibility(View.VISIBLE);
                                    textView = (TextView) findViewById(R.id.textView2);
                                    textView.setText("离线数量有" + Integer.toString(num * 10) + "bytes");
                                    confirmBtn = (Button) findViewById(R.id.button);
                                    confirmBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.setVisibility(View.GONE);
                                        }
                                    });
*/                                    
                                }
                              
                            });
                        }
                    });
                }

                @Override
                public void onReceiveOIDSize(final int OIDSize) {
                    Log.i("TEST1", "-----read OIDSize=====" + OIDSize);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            gCurPageID = -1;
                        	 //showInftTextView.setText("点读！点读值为："+OIDSize);
                        }
                    });
                }

                @Override
                public void onReceiveOfflineProgress(final int i) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
/*                        	
                            if (startOffline) {
                            	
                                layout.setVisibility(View.VISIBLE);
                                text.setText("开始缓存离线数据");
                                bar.setProgress(i);
                                Log.e(TAG, "onReceiveOfflineProgress----" + i);
                                if (i == 100) {
                                    layout.setVisibility(View.GONE);
                                    bar.setProgress(0);
                                }
                            } else {
                                layout.setVisibility(View.GONE);
                                bar.setProgress(0);
                            }
  */                      
                            }
                        
                    });
                }

                @Override
                public void onDownloadOfflineProgress(final int i) {

                }

                @Override
                public void onReceivePenLED(final byte color) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "receive led is " + color);
                            switch (color) {
                                case 1: // blue
                                    gColor = 5;
                                    break;
                                case 2: // green
                                    gColor = 3;
                                    break;
                                case 3: // cyan
                                    gColor = 8;
                                    break;
                                case 4: // red
                                    gColor = 1;
                                    break;
                                case 5: // magenta
                                    gColor = 7;
                                    break;
                                case 6: // yellow
                                    gColor = 2;
                                    break;
                                case 7: // white
                                    gColor = 6;
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                }

                @Override
                public void onOfflineDataNumCmdResult(boolean success) {
                    //Log.i(TAG, "onOfflineDataNumCmdResult---------->" + success);
                }

                @Override
                public void onDownOfflineDataCmdResult(boolean success) {
                    //Log.i(TAG, "onDownOfflineDataCmdResult---------->" + success);
                }

                @Override
                public void onWriteCmdResult(int code) {
                    //Log.i(TAG, "onWriteCmdResult---------->" + code);
                }

                @Override
                public void onReceivePenType(int type) {
                    //Log.i(TAG, "onReceivePenType type---------->" + type);
                    penType = type;
                }
            });
        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };
    public void drawInit() {

        bDrawl[0].initDraw();
        bDrawl[0].setVcolor(Color.WHITE);
        bDrawl[0].setVwidth(1);

        SetPenColor(gColor);
        bDrawl[0].paint.setStrokeCap(Paint.Cap.ROUND);
        bDrawl[0].paint.setStyle(Paint.Style.FILL);
        bDrawl[0].paint.setAntiAlias(true);
        bDrawl[0].invalidate();

    }	
    public void SetPenColor(int ColorIndex) {
        switch (ColorIndex) {
            case 0:
                bDrawl[0].paint.setColor(Color.GRAY);
                return;
            case 1:
                bDrawl[0].paint.setColor(Color.RED);
                return;
            case 2:
                bDrawl[0].paint.setColor(Color.rgb(192, 192, 0));
                return;
            case 3:
                bDrawl[0].paint.setColor(Color.rgb(0, 128, 0));
                return;
            case 4:
                bDrawl[0].paint.setColor(Color.rgb(0, 0, 192));
                return;
            case 5:
                bDrawl[0].paint.setColor(Color.BLUE);
                return;
            case 6:
                bDrawl[0].paint.setColor(Color.BLACK);
                return;
            case 7:
                bDrawl[0].paint.setColor(Color.MAGENTA);
                return;
            case 8:
                bDrawl[0].paint.setColor(Color.CYAN);
                return;
        }
        return;
    }
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 //       setContentView(R.layout.draw);        
//        textView=findViewById(R.id.maintextview);
        Intent gattServiceIntent = new Intent(this, BluetoothLEService.class);
        boolean bBind = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        bDrawl[0] = new DrawView(this);
        bDrawl[0].setVcolor(Color.YELLOW);
        
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;

        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        Log.e(TAG, "density=======>" + density + ",densityDpi=======>" + densityDpi);
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (mWidth / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (mHeight / density);// 屏幕高度(dp)
        Log.e(TAG, "width=======>" + screenWidth);
        Log.e(TAG, "height=======>" + screenHeight);

        Log.e(TAG, "-----screen pixel-----width:" + mWidth + ",height:" + mHeight);

 //       gLayout = (RelativeLayout) findViewById(R.id.mylayout);
 //       gLayout = (RelativeLayout) findViewById(R.id.mylayout);
        RelativeLayout mreLayout=new RelativeLayout(this);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        param.width = (int) mWidth;
        param.height = (int) mHeight-100;
        param.rightMargin = 1;
        param.bottomMargin = 1;
//        param.topMargin=300;
        showInftTextView=(TextView) findViewById(R.id.maintextview);

        showInftTextView.setText(" ");
        //showInftTextView.setText("点信息显示区域");
//        mreLayout.setBackgroundColor(Color.WHITE);
        mreLayout.addView(bDrawl[0], param);
     this.addContentView(mreLayout, param);
        drawInit();
    
        /*        
        gLayout = (RelativeLayout) findViewById(R.cid.mylayout);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        param.width = (int) mWidth;
        param.height = (int) mHeight;
        param.rightMargin = 1;
        param.bottomMargin = 1;
        drawInit();       
        */        
        
        //wsk 2019.3.21
        if (IsTableExist("/sdcard/shiyanke.db","chirographys")) {//打开或创建数据库，并判断表是否存在
			database.execSQL("create table answer(" 
					+"id integer primary key autoincrement,"
                    +"bookID integer,"
                    +"pageID integer,"
                    +"x integer,"
                    +"y integer,"
                    +"fx float,"
                    +"fy float)");
		}
    }
    
    //wsk 2019.3.21
  //判断表是否存在
  	private boolean IsTableExist(String databseNameString ,String tableName) {	
  				boolean isTableExist=true;
//  				Log.e("zgm", "20181222:表存在吗？");
                   database=openOrCreateDatabase(databseNameString, 0, null);
  				Cursor c=database.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='"+tableName+"'", null);
  				
  				  if(c.moveToFirst()){
//  					  Log.e("zgm", "20181222:c.getCount()="+c.getInt(0));
  						if (c.getInt(0)==0) {
  							isTableExist=false;
  						}					  
  					  }

//  				c.close();  
//  				smartPenDatabase.close();
  				return isTableExist;
  		 }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
//        gImageView = (ImageView) findViewById(R.id.imageView2);//得到ImageView对象的引用
 //       gImageView.setScaleType(ImageView.ScaleType.FIT_XY);

        //计算
        float ratio = 1f;
        ratio = (float) ((ratio * mWidth) / A5_BG_REAL_WIDTH);
        BG_WIDTH = (int) (A5_BG_REAL_WIDTH * ratio);
        BG_HEIGHT = (int) (A5_BG_REAL_HEIGHT * ratio);

//        gcontentLeft = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getLeft();
//        gcontentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

//        A5_X_OFFSET = (int) (mWidth - gcontentLeft - BG_WIDTH) / 2;
//        A5_Y_OFFSET = (int) (mHeight - gcontentTop - BG_HEIGHT) / 2;
        //mHandler.sendEmptyMessage(UPDATE_UI_OFFSET);
        A5_X_OFFSET = 20;
        A5_Y_OFFSET = 100;        
        RunReplay();

        
        
        
        
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        bleManager = PenCommAgent.GetInstance(getApplication());

        // 0-free format;1-for A4;2-for A3
        //Log.i(TAG, "-----------setDataFormat-------------");
   //     bleManager.setXYDataFormat(1);//设置码点输出规格  //PC

        switch (item.getItemId()) {  

            case R.id.action_settings:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, SelectDeviceActivity.class);
                startActivityForResult(serverIntent, REQUEST_SELECT_DEVICE);
                return true;
/*                
            case R.id.clear:
                drawInit();
                bDrawl[0].canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                if (!bIsReply) {
                    dot_number.clear();
                    dot_number1.clear();
                    dot_number2.clear();
                    dot_number4.clear();
                }
                
                return true;
 */               
        }
        return false;
    }
 
    
    
    
     
    
    
    
    
/*
 * 将原始点数据进行处理（主要是坐标变换）并保存    
 */
    
    private void ProcessEachDot(Dot dot) {
//        float ratio = 0.95f;
    	float ratio = 0.95f;    	
    	
    	float A_45_ratio = (float) (A5_WIDTH/A4_WIDTH);
    	Log.e("zgm", "A5_WIDTH/A4_WIDTH="+(A5_WIDTH/A4_WIDTH));
		float ax = (float) (A5_WIDTH / XDIST_PERUNIT); // A5纸张宽度方向包含多少个编码单元		
		float ay = (float) (A5_HEIGHT / YDIST_PERUNIT);
		
		Log.i(TAG, "111 ProcessEachDot=" + dot.toString());
		
        if (dot.BookID!=gCurBookID||dot.PageID!= gCurPageID) {
        	Log.e("zgm", "dot.BookID:"+dot.BookID+" "+"gCurBookID"+gCurBookID);
        	bDrawl[0].canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.ADD);//清除画布 PC
            gCurBookID=dot.BookID;
            gCurPageID=dot.PageID;
       
        
        }             
 		
        if (dot.BookID==100) {
          
            ratio = (float) ((ratio * mWidth) / A5_BG_REAL_WIDTH);
            BG_WIDTH = (int) (A5_BG_REAL_WIDTH * ratio);
            BG_HEIGHT = (int) (A5_BG_REAL_HEIGHT * ratio);

        	ax = (float) (A5_WIDTH / XDIST_PERUNIT);
        	ay = (float) (A5_HEIGHT / YDIST_PERUNIT);
        	  Log.e("zgm", "book.id"+dot.BookID+" A5_BG_REAL_WIDTH:"+A5_BG_REAL_WIDTH);
        	
        	
        }		
		
        if (dot.BookID==0) {

            ratio = (float) ((ratio * mWidth) / A0_BG_REAL_WIDTH);
            BG_WIDTH = (int) (A0_BG_REAL_WIDTH * ratio);  //背景图宽=实际背景宽/比率
            BG_HEIGHT = (int) (A0_BG_REAL_HEIGHT * ratio);
/*
 * //115是B5每页上的最大横坐标，133是A4每页上的最大横坐标
 * 因为相当苦逼的是我发现A4纸上的最大横坐标和B5纸上的最大横坐标的比值不等于A4纸宽度于B5纸的宽度，然而B5纸能够很好映射到屏幕，对A4纸只有乘以其坐标和B5坐标的比值，进行缩放了
 */
            ax = (float) ((float) (A5_WIDTH / XDIST_PERUNIT)*(560.0/115));    //A5纸张宽度方向包含多少个编码单元  //PC
            ay = (float) ((float) (A5_HEIGHT /YDIST_PERUNIT)*(560.0/115));    //编码单元数=本子宽（高）/码点宽（高）
            Log.e("zgm", "book.id"+dot.BookID+" BG_WIDTH:"+BG_WIDTH);
         }     	    	
      	Log.i(TAG, "111 ProcessEachDot=" + dot.toString());
      	
      	//处理dot数据
        int counter = 0;
        pointZ = dot.force;
        counter = dot.Counter;
        Log.i("zgm","BookID:  "+dot.BookID );
        Log.i("zgm","Counter: "+dot.Counter );
        Log.i("zgm","Counter: "+dot.force );
        if (pointZ < 0) {
            //Log.i(TAG, "Counter=" + counter + ", Pressure=" + pointZ + "  Cut!!!!!");
            return;
        }

        int tmpx = dot.x;     //整数部分坐标
        pointX = dot.fx;      //小数部分坐标
        pointX /= 100.0;
        pointX += tmpx;

        int tmpy = dot.y;
        pointY = dot.fy;
        pointY /= 100.0;
        pointY += tmpy;

        gpointX = pointX;
        gpointY = pointY;
//         ax = (float) (A5_WIDTH / XDIST_PERUNIT); 
//        float ay = (float) (A5_HEIGHT / YDIST_PERUNIT); 
        
        
        pointX *= (BG_WIDTH);   // 坐标转
        pointX /= ax;			// 换规则
    
        pointY *= (BG_HEIGHT);
        Log.e("zgm", "BG_WIDTH:"+BG_WIDTH+" "+"BG_HEIGHT:"+BG_HEIGHT);
        pointY /= ay; 
        
        
        pointX += A5_X_OFFSET;   //笔迹X轴偏移量
        pointY += A5_Y_OFFSET;   //笔迹y轴偏移量
        
   
        Log.e("zgm", "ax:"+ax+" "+"ay:"+ay);
        Log.e("zgm", "A5_X_OFFSET:"+A5_X_OFFSET+" "+"A5_Y_OFFSET:"+A5_Y_OFFSET);
       if (isSaveLog) {
            saveOutDotLog(dot.BookID, dot.PageID, pointX, pointY, dot.force, 1, gWidth, gColor, dot.Counter, dot.angle);
        }

        if (pointZ > 0)  
        {
            if (dot.type == Dot.DotType.PEN_DOWN) {
                //Log.i(TAG, "PEN_DOWN");
                gPIndex = 0;
                int PageID, BookID;
                PageID = dot.PageID;
                BookID = dot.BookID;
                if (PageID < 0 || BookID < 0) {
                    // 谨防笔连接不切页的情况
                    return;
                }
                //showInftTextView.setText("x坐标："+dot.x+"\n"+"y坐标："+dot.y);  
          
                //Log.i(TAG, "PageID=" + PageID + ",gCurPageID=" + gCurPageID + ",BookID=" + BookID + ",gCurBookID=" + gCurBookID);
                if (PageID != gCurPageID || BookID != gCurBookID) {
                    gbSetNormal = false;
                    SetBackgroundImage(BookID, PageID);
//                    gImageView.setVisibility(View.VISIBLE);
                    bIsOfficeLine = true;
                    gCurPageID = PageID;
                    gCurBookID = BookID;
                    drawInit();
                    DrawExistingStroke(gCurBookID, gCurPageID);
                }

                SetPenColor(gColor);
                drawSubFountainPen2(bDrawl[0], gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 0,dot.PageID);
                //drawSubFountainPen3(bDrawl[0], gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ);


                // 保存屏幕坐标，原始坐标会使比例缩小
                saveData(gCurBookID, gCurPageID, pointX, pointY, pointZ, 0, gWidth, gColor, dot.Counter, dot.angle);
                mov_x = pointX;
                mov_y = pointY;
                return;
            }

            if (dot.type == Dot.DotType.PEN_MOVE) {
                //Log.i(TAG, "PEN_MOVE");
                //gPIndex = 0;
                // Pen Move
                gPIndex += 1;
                mN += 1;
                mov_x = pointX;
                mov_y = pointY;
                //showInftTextView.setText("x坐标："+dot.x+"\n"+"y坐标："+dot.y+"\n"+"BookID："+dot.BookID+"\n"+"PageID："+dot.PageID); 
                SetPenColor(gColor);
                drawSubFountainPen2(bDrawl[0], gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 1,dot.PageID);
                //drawSubFountainPen3(bDrawl[0], gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ);
                bDrawl[0].invalidate();
                // 保存屏幕坐标，原始坐标会使比例缩小
                saveData(gCurBookID, gCurPageID, pointX, pointY, pointZ, 1, gWidth, gColor, dot.Counter, dot.angle);
            }
        } else if (dot.type == Dot.DotType.PEN_UP) {
            //Log.i(TAG, "PEN_UP");
            // Pen Up
        	
        	//wsk 2019.3.21
        	String sqlstr = "INSERT INTO answer (bookID, pageID,x,y," +
			    		"fx,fy) VALUES (?,?,?,?,?,?)";
     	 Object[] args = new Object[]{dot.BookID,dot.PageID,dot.x,
			    		dot.y, dot.fx,dot.fy};
     	 
     	 try
     	 {
		      database.execSQL(sqlstr,args);  
		  } 
     	 catch (SQLException ex) 
     	 {  
//            Log.e("zgm", "0117:ex.getMessage():"+ex.getMessage());
		 } 		
     	 
            if (dot.x == 0 || dot.y == 0) {
                pointX = mov_x;
                pointY = mov_y;
            }

            gPIndex += 1;
            drawSubFountainPen2(bDrawl[0], gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 2,dot.PageID);
            //drawSubFountainPen3(bDrawl[0], gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ);
            // 保存屏幕坐标，原始坐标会使比例缩小
            saveData(gCurBookID, gCurPageID, pointX, pointY, pointZ, 2, gWidth, gColor, dot.Counter, dot.angle);
            bDrawl[0].invalidate();

            pointX = 0;
            pointY = 0;
            mN = 0;
            gPIndex = -1;
            
            
            
        }
    }

/*
 * 根据传入的原始点的部分属性重新打包成Dots，并存放在bookID对应的dot_number dot_number类型为ArrayListMultimap<Integer, Dots>   
 */
    private void saveData(Integer bookID, Integer pageID, float pointX, float pointY, int force, int ntype, int penWidth, int color, int counter, int angle) {
        Log.i(TAG, "======savaData pageID======" + pageID + "========sdfsdf" + angle);
        Dots dot = new Dots(bookID, pageID, pointX, pointY, force, ntype, penWidth, color, counter, angle);

        try {
            if (bookID == 100) {
                dot_number.put(pageID, dot);
            } else if (bookID == 0) {
                dot_number1.put(pageID, dot);
            } else if (bookID == 1) {
                dot_number2.put(pageID, dot);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    
    private void ProcessDots(Dot dot) {
        ////Log.i(TAG, "=======222draw dot=======" + dot.toString());
/*
        // 回放模式，不接受点
        if (bIsReply) {
            return;
        }
*/
        ProcessEachDot(dot);

    }

    private void saveOutDotLog(Integer bookID, Integer pageID, float pointX, float pointY, int force, int ntype, int penWidth, int color, int counter, int angle) {
        //Log.i(TAG, "======savaData pageID======" + pageID + "========sdfsdf" + angle);
        Dots dot = new Dots(bookID, pageID, pointX, pointY, force, ntype, penWidth, color, counter, angle);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        String str1 = formatter1.format(curDate);
        String hh = str.substring(0, 2);

        if (!gStrHH.equals(hh)) {
            //Log.i(TAG, "sssssss " + gStrHH + " " + hh);
            gStrHH = hh;
            bLogStart = true;
        }

        String txt = str + "BookID: " + bookID + " PageID: " + pageID + " Counter: " + counter + "  pointX: " + gpointX + "  pointY: " + gpointY + "  force: " + force + "  angle: " + angle;
        String fileName = str1 + gStrHH + ".log";
        if (isSaveLog) {
            if (bLogStart) {
                BLEFileUtil.writeTxtToFile("-------------------------TQL SmartPen LOG--------------------------", LOGPATH, fileName);
                bLogStart = false;
            }

            BLEFileUtil.writeTxtToFile(txt, LOGPATH, fileName);
        }
    }
    
    public void DrawExistingStroke(int BookID, int PageID) {
        if (BookID == 100) {
            dot_number4 = dot_number;
        } else if (BookID == 0) {
            dot_number4 = dot_number1;
        } else if (BookID == 1) {
            dot_number4 = dot_number2;
        }

        if (dot_number4.isEmpty()) {
            return;
        }

        Set<Integer> keys = dot_number4.keySet();
        for (int key : keys) {
            //Log.i(TAG, "=========pageID=======" + PageID + "=====Key=====" + key);
            if (key == PageID) {
                List<Dots> dots = dot_number4.get(key);
                for (Dots dot : dots) {
                    //Log.i(TAG, "=========pageID=======" + dot.pointX + "====" + dot.pointY + "===" + dot.ntype);

                    drawSubFountainPen1(bDrawl[0], gScale, gOffsetX, gOffsetY, dot.penWidth, dot.pointX, dot.pointY, dot.force, dot.ntype, dot.ncolor);
                }
            }
        }

        bDrawl[0].postInvalidate();
        gPIndex = -1;
    }
    
    private void drawSubFountainPen1(DrawView DV, float scale, float offsetX, float offsetY, int penWidth, float x, float y, int force, int ntype, int color) {
        if (ntype == 0) {
            g_x0 = x;
            g_y0 = y;
            g_x1 = x;
            g_y1 = y;
            //Log.i(TAG, "--------draw pen down-------");
        }

        if (ntype == 2) {
            g_x1 = x;
            g_y1 = y;
            Log.i("TEST", "--------draw pen up--------");
            //return;
        } else {
            g_x1 = x;
            g_y1 = y;
            //Log.i(TAG, "--------draw pen move-------");
        }

        DV.paint.setStrokeWidth(penWidth);
        SetPenColor(color);
        DV.canvas.drawLine(g_x0, g_y0, g_x1, g_y1, DV.paint);
        g_x0 = g_x1;
        g_y0 = g_y1;

        return;
    }
   
    
    private void drawSubFountainPen2(DrawView DV, float scale, float offsetX, float offsetY, int penWidth, float x, float y, int force, int ntype,int pageid) {
    	Log.e("zgm", "执行函数drawSubFountainPen2"); 
    	if(pageid==0) //44
    	{	

    	if (ntype == 0) {
            g_x0 = x;
            g_y0 = y;
            g_x1 = x;
            g_y1 = y;
            //Log.i(TAG, "--------draw pen down-------");
        }
        if (ntype == 2) {
            g_x1 = x;
            g_y1 = y;
            Log.i("TEST", "--------draw pen up--------");
        } else {
            g_x1 = x;
            g_y1 = y;
            //Log.i(TAG, "--------draw pen move-------");
        }

        DV.paint.setStrokeWidth(penWidth);
        DV.canvas.drawLine(g_x0, g_y0, g_x1, g_y1, DV.paint);   //设置画笔 和 位图宽高  
        DV.invalidate();
        g_x0 = g_x1;
        g_y0 = g_y1;

        return;
    }
    	
    	//pageid==1  PC
    	if(pageid==2) {
    		float temptx;
    		float tempty;
    		temptx = (float) (x + 728.0);
    		tempty = (float) y ;//(y + 1024.0);
    		
    		
    	if (ntype == 0) {
            g_x0 = temptx;
            g_y0 = tempty;
            g_x1 = temptx;
            g_y1 = tempty;
            //Log.i(TAG, "--------draw pen down-------");
        }
        if (ntype == 2) {
            g_x1 = temptx;
            g_y1 = tempty;
            Log.i("TEST", "--------draw pen up--------");
        } else {
            g_x1 = temptx;
            g_y1 = tempty;
            //Log.i(TAG, "--------draw pen move-------");
        }

        DV.paint.setStrokeWidth(penWidth);
        DV.canvas.drawLine(g_x0, g_y0, g_x1, g_y1, DV.paint);   //设置画笔 和 位图宽高  
        DV.invalidate();
        g_x0 = g_x1;
        g_y0 = g_y1;

        return;
    }
    	
    	//pageid==3  PC
    	if(pageid==4) {
    		float temptx;
    		float tempty;
    		temptx = (float) x;//(x + 728.0);
    		tempty = (float) (y + 1024.0);
    		
    		
    	if (ntype == 0) {
            g_x0 = temptx;
            g_y0 = tempty;
            g_x1 = temptx;
            g_y1 = tempty;
            //Log.i(TAG, "--------draw pen down-------");
        }
        if (ntype == 2) {
            g_x1 = temptx;
            g_y1 = tempty;
            Log.i("TEST", "--------draw pen up--------");
        } else {
            g_x1 = temptx;
            g_y1 = tempty;
            //Log.i(TAG, "--------draw pen move-------");
        }

        DV.paint.setStrokeWidth(penWidth);
        DV.canvas.drawLine(g_x0, g_y0, g_x1, g_y1, DV.paint);   //设置画笔 和 位图宽高  
        DV.invalidate();
        g_x0 = g_x1;
        g_y0 = g_y1;

        return;
    }
    	
    	//pageid==3  PC
    	if(pageid==6) {
    		float temptx;
    		float tempty;
    		temptx = (float) (x + 728.0);
    		tempty = (float) (y + 1024.0);
    	if (ntype == 0) {
            g_x0 = temptx;
            g_y0 = tempty;
            g_x1 = temptx;
            g_y1 = tempty;
            //Log.i(TAG, "--------draw pen down-------");
        }
        if (ntype == 2) {
            g_x1 = temptx;
            g_y1 = tempty;
            Log.i("TEST", "--------draw pen up--------");
        } else {
            g_x1 = temptx;
            g_y1 = tempty;
            //Log.i(TAG, "--------draw pen move-------");
        }

        DV.paint.setStrokeWidth(penWidth);
        DV.canvas.drawLine(g_x0, g_y0, g_x1, g_y1, DV.paint);   //设置画笔 和 位图宽高  
        DV.invalidate();
        g_x0 = g_x1;
        g_y0 = g_y1;

        return;
    }
    	
    	
    }
    	
    
/*
 * onActivityResult用来接收Intent的数据
 */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_DEVICE:
                //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    try {
                        boolean flag = mService.connect(deviceAddress);
                        penAddress = deviceAddress;
                        // TODO spp
                        //bleManager.setSppConnect(deviceAddress);
                    } catch (Exception e) {
                        //Log.i(TAG, "connect-----" + e.toString());
                    }
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case GET_FILEPATH_SUCCESS_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String path = "";
                    Uri uri = data.getData();
/*
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        String pathFromURI = null;
                        try {
                            pathFromURI = getRealPathFromURI(uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String[] split = pathFromURI.split("/");
                        path = split[split.length - 1];
                    } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
                        String uriPath = uri.getPath();
                        String[] split = uriPath.split("/");
                        path = split[split.length - 1];
                    }
                    */
                    final String str = path;
                    //Log.i(TAG, "onActivityResult: path="+str);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            bleManager.readTestData(str);
                        }
                    }).start();
                }
                break;
            default:
                Log.e(TAG, "wrong request code");
                break;
        }
    }

    private void SetBackgroundImage(int BookID, int PageID) {
 /*   	
        if (!gbSetNormal) {
            LayoutParams para;
            para = gImageView.getLayoutParams();
            para.width = BG_WIDTH;
            para.height = BG_HEIGHT;
            gImageView.setLayoutParams(para);
            gbSetNormal = true;

            //Log.i(TAG, "testOffset BG_WIDTH = " + BG_WIDTH + ", BG_HEIGHT =" + BG_HEIGHT + ", gcontentLeft = " + gcontentLeft + ", gcontentTop = " + gcontentTop);
            //Log.i(TAG, "testOffset A5_X_OFFSET = " + A5_X_OFFSET + ", A5_Y_OFFSET = " + A5_Y_OFFSET);
            //Log.i(TAG, "testOffset mWidth = " + mWidth + ", mHeight = " + mHeight);
            //Log.i(TAG, "testOffset getTop = " + gImageView.getTop() + ", getLeft = " + gImageView.getLeft());
            //Log.i(TAG, "testOffset getWidth = " + gImageView.getWidth() + ", getHeight = " + gImageView.getHeight());
            //Log.i(TAG, "testOffset getMeasuredWidth = " + gImageView.getMeasuredWidth() + ", getMeasuredHeight = " + gImageView.getMeasuredHeight());
        }

        gbCover = true;
        bDrawl[0].canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if (BookID == 168) {
            if (getResources().getIdentifier("p" + PageID, "drawable", getPackageName()) == 0) {
                return;
            }
            gImageView.setImageResource(getResources().getIdentifier("p" + PageID, "drawable", getPackageName()));
        } else if (BookID == 100) {
            if (getResources().getIdentifier("p" + PageID, "drawable", getPackageName()) == 0) {
                return;
            }
            gImageView.setImageResource(getResources().getIdentifier("p" + PageID, "drawable", getPackageName()));
        } else if (BookID == 0) {
            if (getResources().getIdentifier("blank" + PageID, "drawable", getPackageName()) == 0) {
                return;
            }
            gImageView.setImageResource(getResources().getIdentifier("blank" + PageID, "drawable", getPackageName()));
        } else if (BookID == 1) {
            if (getResources().getIdentifier("zhen" + PageID, "drawable", getPackageName()) == 0) {
                return;
            }
            gImageView.setImageResource(getResources().getIdentifier("zhen" + PageID, "drawable", getPackageName()));
        }
   
*/
    }

    public void RunReplay() {
        if (gCurPageID < 0) {
            bIsReply = false;
            return;
        }

        drawInit();
        bDrawl[0].canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ReplayCurrentPage(gCurBookID, gCurPageID, gSpeed);
            }
        }).start();
    }

    public void ReplayCurrentPage(int BookID, int PageID, int SpeedID) {
        if (BookID == 100) {
            dot_number4 = dot_number;
        } else if (BookID == 0) {
            dot_number4 = dot_number1;
        } else if (BookID == 1) {
            dot_number4 = dot_number2;
        }

        if (dot_number4.isEmpty()) {
            bIsReply = false;
            return;
        }

        Set<Integer> keys = dot_number4.keySet();
        for (int key : keys) {
            //Log.i(TAG, "=========pageID=======" + PageID + "=====Key=====" + key);
            bIsReply = true;
            if (key == PageID) {
                List<Dots> dots = dot_number4.get(key);
                for (Dots dot : dots) {
                    //Log.i(TAG, "=========pageID1111=======" + dot.pointX + "====" + dot.pointY + "===" + dot.ntype);
                    drawSubFountainPen1(bDrawl[0], gScale, gOffsetX, gOffsetY, dot.penWidth, dot.pointX, dot.pointY, dot.force, dot.ntype, dot.ncolor);
                    //drawSubFountainPen3(bDrawl[0], gScale, gOffsetX, gOffsetY, dot.penWidth, dot.pointX, dot.pointY, dot.force);

                    bDrawl[0].postInvalidate();
                    SystemClock.sleep(SpeedID);
                }
            }
        }

        bIsReply = false;

        gPIndex = -1;
        return;
    }
    
    
}
