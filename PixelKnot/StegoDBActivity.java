package info.guardianproject.pixelknot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import info.guardianproject.f5android.plugins.f5.james.JpegEncoder;
import info.guardianproject.f5android.stego.StegoProcessThread;
import info.guardianproject.pixelknot.crypto.Aes;

public class StegoDBActivity extends AppCompatActivity {

    Dummy dummy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stego_db);

        getPermissions();
        dummy = new Dummy(App.getInstance());

        StegoJob job = new StegoJob(App.getInstance());
        job.addProcess(new Runnable(){
            @Override
            public void run()
            {
                //add stuff from here
                originalPixelKnot(
                        "HELLO",
                        "123456",
                        getCacheDir()+"/input.jpg",
                        "output.jpg"
                );
            }
        }, 1);
        job.Run();
    }


    private void originalPixelKnot(String message, String password, String input_image_path, String output_image_name)
    {
        // 1. Load Image to Bitmap
        Bitmap bmp = loadImage(input_image_path);

        // 2. Encrypt message with first 2 thirds of the password
        String aes_key = password.substring(0, password.length()/3);
        String aes_salt = password.substring(password.length()/3, (password.length()/3)*2);
        Map.Entry<String, String> pack =
                Aes.EncryptWithPassword(aes_key, message, aes_salt.getBytes())
                    .entrySet().iterator().next();
        String aes_iv = pack.getKey();
        String aes_ciphertext = pack.getValue();
        String f5_payload = Constants.PASSWORD_SENTINEL + aes_iv + aes_ciphertext;

        // 3. Run F5Android using the last third of the password as seed
        String f5_seed = password.substring((password.length() / 3) * 2);
        runF5Android(f5_payload, f5_seed, bmp, output_image_name);
    }

    private void runF5Android(String message, String seed, Bitmap bmp, String outputName)
    {
        File outputImage = new File(getCacheDir().getAbsolutePath() + "/" + outputName);
        try
        {
            FileOutputStream fos = new FileOutputStream(outputImage);
            JpegEncoder je = new JpegEncoder(
                    dummy, bmp, Constants.OUTPUT_IMAGE_QUALITY,
                    fos, seed.getBytes(), new StegoProcessThread());
            boolean success = je.Compress(new ByteArrayInputStream(message.getBytes()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private Bitmap loadImage(String path)
    {
        try
        {
            return Picasso.with(getApplicationContext())
                    .load(new File(path))
                    .resize(Constants.MAX_IMAGE_PIXEL_SIZE, Constants.MAX_IMAGE_PIXEL_SIZE)
                    .onlyScaleDown()
                    .centerInside()
                    .get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    void getPermissions()
    {
        int read = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (read != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    0);
        }
        if (write != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }
    }
}
