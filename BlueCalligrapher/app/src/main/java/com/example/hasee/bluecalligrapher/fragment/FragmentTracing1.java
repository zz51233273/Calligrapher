package com.example.hasee.bluecalligrapher.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.RecognizeService;
import com.example.hasee.bluecalligrapher.adapter.TracingOcrListAdapter;
import com.example.hasee.bluecalligrapher.bean.Store;
import com.example.hasee.bluecalligrapher.decodebase64.DecodeBase64;
import com.example.hasee.bluecalligrapher.main.MainActivity;
import com.example.hasee.bluecalligrapher.tracing.CharCompareActivity;
import com.example.hasee.bluecalligrapher.tracing.PhotoCompareActivity;
import com.example.hasee.bluecalligrapher.tracing.TracingLibraryActivity;
import com.example.hasee.bluecalligrapher.utils.FileUtil;
import com.example.hasee.bluecalligrapher.utils.ImageUtil;
import com.example.hasee.bluecalligrapher.utils.StringUtil;
import com.example.hasee.bluecalligrapher.write_field_character.FieldCharacterShapeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by hasee on 2018/3/30.
 */

public class FragmentTracing1 extends Fragment implements View.OnClickListener{
    private View view;
    private static ImageView showImg1;
    private static ImageView love;
    private ImageView tracing_library;
    private ImageView tracing_ocr;  //文字识别图标
    private ImageView tracing_write;    //手写图标
    private static boolean isLove=false;
    private final int ADD_STORE=0;
    private final int DELETE_STORE=1;
    private final int GETCHAR_OCR=2;
    private final int REQUEST_CODE_GENERAL = 105;
    private final int REQUEST_CODE_GENERAL_BASIC = 106;
    private final int REQUEST_CODE_ACCURATE_BASIC = 107;
    private final int CODE_ORIGINAL_PHOTO_CAMERA=108;
    private final int GETCOMPARE_OCR=5;
    private final int OPEN_CAMERA_COMPARE =4;
    private final int SEARCH_FAILED =6;
    private final int SEARCH_SUCCESS =7;
    private Bitmap compare_photo;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ADD_STORE:
                    addStore();
                    break;
                case DELETE_STORE:
                    deleteStore();
                    break;
                case GETCHAR_OCR:
                    getcharacter_ocr((String)msg.obj);
                    break;
                case GETCOMPARE_OCR:
                    getCompare_ocr((String)msg.obj);
                    break;
                case SEARCH_FAILED:
                    Toast.makeText(getContext(),"字库中暂无",Toast.LENGTH_SHORT).show();
                    break;
                case SEARCH_SUCCESS:
                    JSONObject jsonObject=(JSONObject)msg.obj;
                    byte[] img_bytes={};
                    try{
                        img_bytes = DecodeBase64.decodeBase(jsonObject.getString("style_jian"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Intent intent=new Intent(getContext(), PhotoCompareActivity.class);
                    intent.putExtra("tracing_img",img_bytes);
                    intent.putExtra("img",ImageUtil.getImgBytes(compare_photo));
                    startActivity(intent);
                    break;
            }
        }
    };
    public FragmentTracing1(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.fg_tracing_jian,container, false);

        showImg1=(ImageView)view.findViewById(R.id.photo_char_jian);
        love=(ImageView)view.findViewById(R.id.fg_tracing_love);
        tracing_write=(ImageView)view.findViewById(R.id.fg_tracing_write);
        tracing_library=(ImageView)view.findViewById(R.id.fg_tracing_library);
        tracing_ocr=(ImageView)view.findViewById(R.id.fg_tracing_ocr);
        tracing_library.setOnClickListener(this);
        tracing_ocr.setOnClickListener(this);
        love.setOnClickListener(this);
        tracing_write.setOnClickListener(this);
        view.findViewById(R.id.fg_tracing_jian_layout).setOnClickListener(this);
        if(null!=Fragment2.tracings[0]){
            setImage(Fragment2.tracings[0].getPicture());
        }
        return view;
    }

    public static void setImage(byte[] image){
        if(image!=null){
            //将字节数组转化为位图
            Bitmap imagebitmap= BitmapFactory.decodeByteArray(image,0,image.length);
            if(null!=showImg1) {
                showImg1.setImageBitmap(imagebitmap);
                if(null!= MainActivity.user){
                    if(MainActivity.storeCharacter.contains(Fragment2.tracings[0].getCharacter())){
                        isLove=true;
                        love.setImageResource(R.drawable.full_love);
                    }else{
                        isLove=false;
                        love.setImageResource(R.drawable.love);
                    }
                }else{
                    isLove=false;
                    love.setImageResource(R.drawable.love);
                }
            }
        }
    }

    @Override
    public void onClick(View v){
        int id=v.getId();
        switch (id){
            case R.id.fg_tracing_love:      //爱心收藏
                loveOrNot();
                break;
            case R.id.fg_tracing_jian_layout:
                //隐藏键盘
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                break;
            case R.id.fg_tracing_library:   //字库
                startActivity(new Intent(getContext(), TracingLibraryActivity.class));
                break;
            case R.id.fg_tracing_ocr:   //文字识别
                createPhotoDialog();
                break;
            case R.id.fg_tracing_write: //手写
                Intent intent=new Intent(getContext(), FieldCharacterShapeActivity.class);
                if(null!=showImg1.getDrawable()){
                    Bitmap image = ((BitmapDrawable)showImg1.getDrawable()).getBitmap();
                    byte[] img_bytes= ImageUtil.getImgBytes(image);
                    intent.putExtra("img_bytes",img_bytes);
                }
                startActivity(intent);
                break;
        }
    }

    //创建选择图片对话框
    private void createPhotoDialog(){
        final Fragment fragment=this;
        final Dialog mCameraDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        LinearLayout root = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.select_ocr_dialog, null);
        root.findViewById(R.id.chooseOcr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(getContext(), CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getContext()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_GENERAL_BASIC);
                mCameraDialog.dismiss();
            }
        });
/*        root.findViewById(R.id.chooseCompare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(getContext(), CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getContext()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, OPEN_CAMERA_COMPARE);
                mCameraDialog.dismiss();
            }
        });*/
        root.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraDialog.dismiss();
            }
        });
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //takePictureManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void loveOrNot(){       //当前是否收藏
        if(null!=MainActivity.user && null!=Fragment2.tracings[0]){
            if(!isLove){
                Message msg=new Message();
                msg.what=ADD_STORE;
                mHandler.sendMessage(msg);
                isLove=true;
            }else{
                Message msg=new Message();
                msg.what=DELETE_STORE;
                mHandler.sendMessage(msg);
                isLove=false;
            }
        }else if(null==MainActivity.user){
            Toast.makeText(getContext(),"您还未登录呢",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(),"您还未查询汉字呢",Toast.LENGTH_SHORT).show();
        }
    }

    private void addStore(){
        love.setImageResource(R.drawable.full_love);
        //storeCharacter中也要加上相应的汉字
        int len=MainActivity.storeCharacter.length();
        MainActivity.storeCharacter=MainActivity.storeCharacter+Fragment2.tracings[0].getCharacter();
        MainActivity.storeCharacter=MainActivity.storeCharacter+Fragment2.tracings[1].getCharacter();
        MainActivity.storeCharacter=MainActivity.storeCharacter+Fragment2.tracings[2].getCharacter();
        MainActivity.storeCharacter=MainActivity.storeCharacter+Fragment2.tracings[3].getCharacter();
        MainActivity.storeStyle=MainActivity.storeStyle+"简繁行草";
        for(int i=len;i<=len+3;i++){
            MainActivity.store[i]=new Store();
            MainActivity.store[i].setPicture(Fragment2.tracings[i-len].getPicture());
        }
        Toast.makeText(getContext(), "已收藏", Toast.LENGTH_LONG).show();
        for(int i=0;i<4;i++){
            final int pos=i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AddStoreRequest(MainActivity.user.getPhoneNumber(),Fragment2.tracings[pos].getId());
                }
            }).start();
        }
    }

    private void deleteStore(){
        love.setImageResource(R.drawable.love);
        //storeCharacter中也要删除相应的汉字
        int pos=MainActivity.storeCharacter.indexOf(Fragment2.tracings[0].getCharacter());
        MainActivity.storeCharacter=MainActivity.storeCharacter.replace(Fragment2.tracings[0].getCharacter(),"");
        MainActivity.storeStyle=MainActivity.storeStyle.substring(0,pos)+MainActivity.storeStyle.substring(pos+4,MainActivity.storeStyle.length());
        Toast.makeText(getContext(),"你不爱她了",Toast.LENGTH_SHORT).show();
        //删除已收藏的字
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteStoreRequest(MainActivity.user.getPhoneNumber(),Fragment2.tracings[0].getId(),Fragment2.tracings[1].getId(),Fragment2.tracings[2].getId(),Fragment2.tracings[3].getId());
            }
        }).start();
    }

    private void getcharacter_ocr(String ocr_chars){
        ocr_chars=ocr_chars.substring(1,ocr_chars.length());
        if(ocr_chars.contains("{")){
            Fragment2.characters.clear();
            ocr_chars=ocr_chars.substring(ocr_chars.indexOf("{"),ocr_chars.length());
            int pos=ocr_chars.indexOf("words");
            while(pos!=-1){
                pos+=9;
                ocr_chars=ocr_chars.substring(pos,ocr_chars.length());//接收到的字符的首位置
                int pos2=ocr_chars.indexOf("\"");
                String characters=ocr_chars.substring(0,pos2);
                int len=characters.length();
                for(int i=0 ; i<len ; i++){
                    char character=characters.charAt(i);
                    if(StringUtil.isChinese(character)){
                        if(!Fragment2.characters.contains(character+""))
                            Fragment2.characters.add(character+"");
                    }
                }
                pos=ocr_chars.indexOf("words");
            }
            Fragment2.tracingOcrAdapter=new TracingOcrListAdapter(getContext(),R.layout.tracing_ocr_item,Fragment2.characters);
            Fragment2.listView.setAdapter(Fragment2.tracingOcrAdapter);
        }else{
            Toast.makeText(getContext(),"未识别到文字",Toast.LENGTH_SHORT).show();
        }
    }

    private void getCompare_ocr(String ocr_chars){
        ocr_chars=ocr_chars.substring(1,ocr_chars.length());
        if(ocr_chars.contains("{")){
            ocr_chars=ocr_chars.substring(ocr_chars.indexOf("{"),ocr_chars.length());
            int pos=ocr_chars.indexOf("words");
            String getCharacter=""; //识别到的汉字
            while(pos!=-1){
                pos+=9;
                ocr_chars=ocr_chars.substring(pos,ocr_chars.length());//接收到的字符的首位置
                int pos2=ocr_chars.indexOf("\"");
                String characters=ocr_chars.substring(0,pos2);
                int len=characters.length();
                if(len>0){
                    for(int i=0 ; i<len ; i++){
                        char character=characters.charAt(i);
                        if(StringUtil.isChinese(character)){
                            getCharacter=character+"";
                            Log.d("test",getCharacter);
                            break;
                        }
                    }
                    if(!getCharacter.equals("")){
                        break;
                    }
                }
                pos=ocr_chars.indexOf("words");
            }
            final String getCharacters=getCharacter;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    searchRequest(getCharacters);
                }
            }).start();

        }else{
            Toast.makeText(getContext(), "未识别到文字", Toast.LENGTH_LONG).show();
        }
    }

    //添加收藏请求
    private void AddStoreRequest(final String phonenumber, final String id){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/AddStoreServlet";    //注①
        String tag = "AddStore";    //注②
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        //取得请求队列
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phonenumber",phonenumber);  //注⑥
                params.put("id",id);
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }

    //删除收藏请求
    private void deleteStoreRequest(final String phonenumber, final String id1,final String id2,final String id3,final String id4){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/DeleteStoreServlet";    //注①
        String tag = "DeleteStore";    //注②
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        //取得请求队列
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phonenumber",phonenumber);  //注⑥
                params.put("id1",id1);
                params.put("id2",id2);
                params.put("id3",id3);
                params.put("id4",id4);
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }


    private boolean checkTokenStatus() {
        if (!MainActivity.hasGotToken) {
            Toast.makeText(getContext(), "token还未成功获取", Toast.LENGTH_LONG).show();
        }
        return MainActivity.hasGotToken;
    }


    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 识别成功回调，通用文字识别
        if (requestCode == REQUEST_CODE_GENERAL_BASIC && resultCode == Activity.RESULT_OK) {
            RecognizeService.recGeneralBasic(FileUtil.getSaveFile(getContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            Message message=Message.obtain();
                            message.obj=result;
                            message.what=GETCHAR_OCR;
                            mHandler.sendMessage(message);
                        }
                    });
        }
        // 识别成功回调，通用文字识别（高精度版）
        if (requestCode == REQUEST_CODE_ACCURATE_BASIC && resultCode == Activity.RESULT_OK) {
            RecognizeService.recAccurateBasic(FileUtil.getSaveFile(getContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            Message message=Message.obtain();
                            message.obj=result;
                            message.what=GETCHAR_OCR;
                            mHandler.sendMessage(message);
                        }
                    });
        }
        if(requestCode==OPEN_CAMERA_COMPARE && resultCode==Activity.RESULT_OK){
            RecognizeService.recGeneralBasic(FileUtil.getSaveFile(getContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            compare_photo = BitmapFactory.decodeFile(FileUtil.getSaveFile(getContext()).getAbsolutePath());
                            Message message=Message.obtain();
                            message.obj=result;
                            message.what=GETCOMPARE_OCR;
                            mHandler.sendMessage(message);
                        }
                    });
        }

        /*else{
            takePictureManager.attachToActivityForResult(requestCode, resultCode, data);
        }*/
    }

    private void searchRequest(final String text){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SearchServlet";    //注①
        String tag = "Search";    //注②
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        //取得请求队列
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("test",response+"");
                        try {
                            JSONObject jsonObject = (JSONObject) new JSONObject(response).get("params");  //注③
                            String result = jsonObject.getString("Result");  //注④
                            if (result.equals("success")) {  //注⑤
                                Message message=Message.obtain();
                                message.obj=jsonObject;
                                message.what=SEARCH_SUCCESS;
                                mHandler.sendMessage(message);

                                //做自己的成功操作，如页面跳转
                            } else {
                                mHandler.sendEmptyMessage(SEARCH_FAILED);
                                //做自己的失败操作
                            }
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            Log.e("TAG", e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("search_text",text);  //注⑥
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }
}
