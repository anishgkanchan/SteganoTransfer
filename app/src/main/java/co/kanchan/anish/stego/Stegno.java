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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Stegno extends ActionBarActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    Bitmap carrier, secret,tempc,temps,temp;
    ImageView iv;
    boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stegno);
        iv=(ImageView)findViewById(R.id.imageView2);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stegno, menu);
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

    public void carrierImage(View view) {
        flag=true;
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Log.d("Helloooooooooooooooooooo","Carrier entered");
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    public void secretImage(View view) {
        flag=false;
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Log.d("Helloooooooooooooooooooo","Secret entered");
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    public void generateImage(View view) throws IOException {

        Log.d("Helloooooooooooooooooooo", "Generate Image entered");
        Toast.makeText(this, "Generating Image. Please wait.", Toast.LENGTH_SHORT).show();
        int height = carrier.getHeight();
        int width = carrier.getWidth();
        int pixelCarrier, pixelSecret;
        int Ac, Rc, Gc, Bc, As, Rs, Gs, Bs;

      //  temp = carrier.copy(Bitmap.Config.ARGB_8888, true);
        tempc=carrier.copy(Bitmap.Config.ARGB_8888, true);
        temps = secret.copy(Bitmap.Config.ARGB_8888, true);
        temp= Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Log.d("HellooooooooMellowwwheight", height + "," + width);
        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 200; j++) {

                pixelCarrier = tempc.getPixel(i, j);
                pixelSecret = temps.getPixel(i, j);
                Rs = Color.red(pixelSecret);
                Gs = Color.green(pixelSecret);
                Bs = Color.blue(pixelSecret);
                int aRs=Rs>>>7;
                int aGs=Gs>>>7;
                int aBs=Bs>>>7;
                Ac = Color.alpha(pixelCarrier);
                Rc = Color.red(pixelCarrier);
                Gc = Color.green(pixelCarrier);
                Bc = Color.blue(pixelCarrier);
                int aRc =Rc>>>1;
                aRc=aRc<<1;
                int aGc =Gc>>>1;
                aGc=aGc<<1;
                int aBc =Bc>>>1;
                aBc=aBc<<1;
                aRc=aRc|aRs;
                aGc=aGc|aGs;
                aBc=aBc|aBs;
                if(j%25==0&&i%25==0) {
                    Log.d("Steganography: ","Encoding");
                    Log.d("RGB carrier Values: ",Rc+" "+Gc+" "+Bc);
                    Log.d("RGB secret Values: ",Rs+" "+Gs+" "+Bs);
                    Log.v("Round is ",i+","+j);
                    Log.v("Bit shift", Rs + " becomes " + aRs + " | " + Gs + " becomes " + aGs + " | " + Bs + " becomes " + aBs);
                    Log.v("Bitwise or", Rc + " becomes " + aRc + " | " + Gc + " becomes " + aGc + " | " + Bc + " becomes " + aBc);
                }
                temp.setPixel(i, j, Color.argb(Ac, aRc, aGc, aBc));
            }
        }
        OutputStream fOut = null;
        File wallpaperDirectory = new File("/sdcard/StegnoFiles/EncodedFiles");
        wallpaperDirectory.mkdirs();
        File file = new File(wallpaperDirectory, ((EditText)findViewById(R.id.editText)).getText().toString()+".png");
        fOut = new FileOutputStream(file);
        temp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
        fOut.flush();
        fOut.close();
        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
        iv.setImageBitmap(temp);


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            if(!flag)
            {
                secret= BitmapFactory.decodeFile(picturePath);
                Log.d("Helloooooooooooooooooooo","Secret updated");
                Toast.makeText(getApplicationContext(),"Bitmap dimensions: "+secret.getHeight()+","+secret.getWidth(),Toast.LENGTH_SHORT).show();
            }
            else
            {
                carrier=BitmapFactory.decodeFile(picturePath);
                Log.d("Helloooooooooooooooooooo","Carrier updated");
                Toast.makeText(getApplicationContext(),"Bitmap dimensions: "+carrier.getHeight()+","+carrier.getWidth(),Toast.LENGTH_SHORT).show();

            }
            Log.d("Helloooooooooooooooooooo","Carrier: "+carrier);
            Log.d("Helloooooooooooooooooooo","Secret: "+secret);
            // String picturePath contains the path of selected Image
        }
    }

}
