package com.example.storagepermissions

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.storagepermissions.databinding.ActivityMainBinding




 class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

     // Show Permission dialog
     lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

     //Take URI of image
     lateinit var resultLauncher : ActivityResultLauncher<Intent>

     //Type of permission
     lateinit var permission : String


     var deniedWithNeverAskAgain = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



     //Manage different permission responses
       val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->

                if (isGranted) {
                    Log.d( "onCreate: ","granted")
                    onPermissionGranted()
                }
                else {
                       if(deniedWithNeverAskAgain)
                           onPermissionDeniedWithDoNotAskAgain()
                       else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                         deniedWithNeverAskAgain = true
                    }

                }
            }


      //  get URI of image
         resultLauncher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
         ) { result ->

             val data = result.data
             if (data != null) {
                 binding.imageUri.text = data.data.toString()
             }

         }

       //button click
        binding.openGallery.setOnClickListener {
            permission = android.Manifest.permission.READ_EXTERNAL_STORAGE

                requestPermissionLauncher.launch(permission)

        }

    }


    private fun onPermissionDeniedWithDoNotAskAgain(){

         val builder = AlertDialog.Builder(this)
            builder.setMessage("Allow permission to use this app Go to settings")
            builder.setPositiveButton("OK") { _, _ ->

                //Handling sdk version of <30

                intent = if(Build.VERSION.SDK_INT<30)
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                else
                    Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)

                    val uri = Uri.fromParts("package",this.packageName,null)
                    intent.data = uri

                    startActivity(intent)

            }
        builder.show()
    }

    private fun onPermissionGranted(){

            Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show()
            intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)


    }


}




