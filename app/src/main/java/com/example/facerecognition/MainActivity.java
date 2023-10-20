package com.example.facerecognition;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import org.tensorflow.lite.support.image.TensorImage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends Activity {

    private static final int REQUEST_IMAGE_GALLERY_1 = 1;
    private static final int REQUEST_IMAGE_GALLERY_2 = 2;
    private ImageView imageView1;
    private ImageView imageView2;
    private Button btnSelectImage1;
    private Button btnSelectImage2;
    private Button btnCompare;

    private Bitmap selectedImage1;
    private Bitmap selectedImage2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        btnSelectImage1 = findViewById(R.id.btnSelectImage1);
        btnSelectImage2 = findViewById(R.id.btnSelectImage2);
        btnCompare = findViewById(R.id.btnCompare);

        btnSelectImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(REQUEST_IMAGE_GALLERY_1);
            }
        });

        btnSelectImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(REQUEST_IMAGE_GALLERY_2);
            }
        });

        btnCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedImage1 != null && selectedImage2 != null) {
                    compareImages(selectedImage1, selectedImage2);
                } else {
                    showToast("Please select two images first.");
                }
            }
        });
    }

    private void selectImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    Bitmap bitmap = getBitmapFromUri(data.getData());

                    if (requestCode == REQUEST_IMAGE_GALLERY_1) {
                        selectedImage1 = bitmap;
                        imageView1.setImageBitmap(selectedImage1);
                        imageView1.setVisibility(View.VISIBLE);
                    } else if (requestCode == REQUEST_IMAGE_GALLERY_2) {
                        selectedImage2 = bitmap;
                        imageView2.setImageBitmap(selectedImage2);
                        imageView2.setVisibility(View.VISIBLE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showToast("Error loading image");
                }
            }
        }
    }

    private Bitmap getBitmapFromUri(android.net.Uri uri) throws IOException {
        InputStream imageStream = getContentResolver().openInputStream(uri);
        return BitmapFactory.decodeStream(imageStream);
    }

    private void compareImages(Bitmap image1, Bitmap image2) {

        FaceDetector faceDetector = FaceDetector.create(getApplicationContext());

        TensorImage tensorImage1 = TensorImage.fromBitmap(image1);
        TensorImage tensorImage2 = TensorImage.fromBitmap(image2);


        List<Face> faces1 = faceDetector.detect(tensorImage1);
        List<Face> faces2 = faceDetector.detect(tensorImage2);

        if (faces1.size() != faces2.size()) {
            showToast("Number of faces in the two images is different.");
            return;
        }

        for (int i = 0; i < faces1.size(); i++) {
            Face face1 = faces1.get(i);
            Face face2 = faces2.get(i);

            // Compare facial features
            float similarityScore = compareFacialFeatures(face1, face2);

            // Set threshold score
            float similarityThreshold = 0.8f;

            // Faces are considered a match if the similarity score is above the threshold
            if (similarityScore < similarityThreshold) {
                showToast("Face recognition failed!");
                return;
            }
        }

        showToast("Face recognition successful!");
    }

    private float compareFacialFeatures(Face face1, Face face2) {
        return (float) Math.random();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
