package co.kanchan.anish.stego;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class MyActivity extends ActionBarActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    String name;
    Bitmap deciphered, source;
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        iv=(ImageView)findViewById(R.id.imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void performSteganography(View view) {
        Intent i = new Intent(this,Stegno.class);
        startActivity(i);
    }
    public void decipher(View view) {
        name=((EditText)findViewById(R.id.editText)).getText().toString();
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            source=BitmapFactory.decodeFile(picturePath);
            int height=source.getHeight();
            int width=source.getWidth();
            int pixel;
            int A,R,G,B;
         //   int rt=1,gt=1,bt=1;
            deciphered=source.copy(Bitmap.Config.ARGB_8888, true);
            for(int i=0;i<height;i++)
            {
                for(int j=0;j<width;j++){
                    pixel=source.getPixel(i,j);
                    A= Color.alpha(pixel);
                    R= Color.red(pixel);
                    G= Color.green(pixel);
                    B= Color.blue(pixel);
                    int Rt=R&1;
                    int Gt=G&1;
                    int Bt=B&1;
                    int Rh=Rt*255;
                    int Gh=Gt*255;
                    int Bh=Bt*255;
                    deciphered.setPixel(i,j,Color.argb(A,Rh,Gh,Bh));
                    if(j%25==0&&i%25==0) {
                        Log.d("Decoding formally!: Round ",i+","+j);
                        Log.d("RGB carrier Values: ", R + " " + G + " " + B);
                        Log.d("RGB secret Values: ",Rt+" "+Gt+" "+Bt);
                        Log.d("RGB secret Values: ",Rh+" "+Gh+" "+Bh);
                    }
                }
            }
            OutputStream fOut = null;
            File wallpaperDirectory = new File("/sdcard/StegnoFiles/DecodedFiles");
            wallpaperDirectory.mkdirs();
            File file = new File(wallpaperDirectory, name+".png");
            try {
                fOut = new FileOutputStream(file);
                deciphered.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();
                MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.d("Big error found!",e.toString()+"");
            }

            iv.setImageBitmap(deciphered);
        }
    }
}
