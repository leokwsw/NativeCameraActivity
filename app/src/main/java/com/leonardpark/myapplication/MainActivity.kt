package com.leonardpark.myapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.leonardpark.myapplication.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setSupportActionBar(binding.toolbar)

    val navController = findNavController(R.id.nav_host_fragment_content_main)
    appBarConfiguration = AppBarConfiguration(navController.graph)
    setupActionBarWithNavController(navController, appBarConfiguration)

    binding.fab.setOnClickListener { view ->
      dispatchTakePhotoIntent()
    }
  }

  val REQUEST_TAKE_PHOTO = 1
  val REQUEST_TAKE_PHOTO_PERMISSION = 1

  private var tempFileUri: Uri? = null

  private fun dispatchTakePhotoIntent() {
    val checkPermissionResult = checkSelfPermission("android.permission.CAMERA")
    if (checkPermissionResult == PackageManager.PERMISSION_GRANTED) {
      val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
      if (takePhotoIntent.resolveActivity(packageManager) != null) {
        val tempFile: File = getTakePhotoFile()
        tempFileUri = FileProvider.getUriForFile(this, "$packageName.provider", tempFile)
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri)

        startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO)
      } else {

      }
    } else {
      requestPermissions(
        arrayOf("android.permission.CAMERA"),
        REQUEST_TAKE_PHOTO_PERMISSION
      )
    }
  }

  private fun getTakePhotoFile(): File {
    val dir = File(getExternalFilesDir(""), "temp")
    if (!dir.exists()) {
      dir.mkdirs()
    }
    val file = File(dir, "taken_photo.jpg")
    if (!file.exists()) {
      try {
        file.createNewFile()
      } catch (ex: Exception) {
        ex.printStackTrace()
      }
    }
    return file
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String?>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    var grantedCount = 0
    for (grantResult in grantResults) {
      grantedCount += if (grantResult == PackageManager.PERMISSION_GRANTED) 1 else 0
    }
    if (requestCode == REQUEST_TAKE_PHOTO_PERMISSION) {
      if (grantedCount == permissions.size) {
        dispatchTakePhotoIntent()
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_TAKE_PHOTO) {
      if (resultCode == RESULT_OK) {
        Log.d("testmo", "tempFileUri" + tempFileUri.toString())
        Log.d("testmo", "data : " + data?.data)
      } else {
        Log.d("testmo", "tempFileUri null")
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data)
    }
  }


  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    return when (item.itemId) {
      R.id.action_settings -> true
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_host_fragment_content_main)
    return navController.navigateUp(appBarConfiguration)
      || super.onSupportNavigateUp()
  }
}