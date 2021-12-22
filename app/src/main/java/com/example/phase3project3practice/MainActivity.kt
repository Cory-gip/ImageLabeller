package com.example.phase3project3practice

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.example.phase3project3practice.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

private lateinit var binding: ActivityMainBinding

private lateinit var imageBitmap: Bitmap



val REQUEST_IMAGE_CAPTURE = 1

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.takePictureButton.setOnClickListener { dispatchTakePictureIntent() }
        binding.imageLabelButton.setOnClickListener { imageLabeling(imageBitmap) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageBitmap = data!!.extras!!.get("data") as Bitmap
            binding.cameraImage.setImageBitmap(imageBitmap)
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Log.i("Error", "Could not open camera")
        }
    }

    private fun imageLabeling(mybitmap: Bitmap) {
        if (mybitmap == null) {
            Log.i("imageLabeling", "PLease take a picture before attempting to label it")
            return
        }
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromBitmap(mybitmap, 0)
        var outputText = ""

        labeler.process(image).addOnSuccessListener { labels ->
            for (label in labels) {
                val text = label.text
                val confidence = label.confidence
                outputText += "$text : $confidence\n"
                Log.i("Labels", "LOOP>     [$text]:$confidence")
            }

            binding.labelsText.text = outputText

        }.addOnFailureListener { e ->
            Log.e("Failure", "**Failure when processing image")
        }
    }

}