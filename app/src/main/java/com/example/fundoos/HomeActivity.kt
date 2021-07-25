package com.example.fundoos

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.dialog_signout_box.view.*
import kotlinx.android.synthetic.main.nav_header.*
import java.io.ByteArrayOutputStream

class HomeActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var mAuth: FirebaseAuth

    private lateinit var profileImage: CircleImageView

    private  val CAMERA_REQUEST_CODE = 123
    private  val GALLERY_REQUEST_CODE = 214

    lateinit var notesAdapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(my_toolbar)

        my_toolbar.showOverflowMenu()

        onTheNaviagtionItemSelected()

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_Container, NotesFragment())
                .setReorderingAllowed(true).addToBackStack(null).commit()
            nav_view.setCheckedItem(R.id.nav_notes)
        }

        mAuth = FirebaseAuth.getInstance()

        profileImage = toolbar_profile_image as CircleImageView

        onClickSelectedItem()

        setupListOfDataIntiRecyclerView()

        val str = intent.getStringExtra("image")
//        eMail_navigationView.setText(str)
        val str1 = intent.getStringExtra("picture")
//        Picasso.with(applicationContext).load(str1).error(R.drawable.note_fundo).into(image_navigationView)
    }

    private fun onClickSelectedItem() {
        profileImage.setOnClickListener {
            selectingImage()
            Toast.makeText(this@HomeActivity, "Image Clicked", Toast.LENGTH_SHORT).show()

            val user = FirebaseAuth.getInstance().currentUser


//            eMail_navigationView.text = user?.email

            if(user?.photoUrl != null) {
                Picasso.with(this@HomeActivity).load(user.photoUrl).into(profileImage)
                Picasso.with(this@HomeActivity).load(user.photoUrl).into(image_navigationView)
            }
        }

        create_new_note.setOnClickListener() {
            supportFragmentManager.beginTransaction().replace(R.id.create_fragment_container, CreateNewNoteFragment())
                .setReorderingAllowed(true).addToBackStack(null).commit()
            Toast.makeText(applicationContext, "Creating New Note", Toast.LENGTH_SHORT).show()

        }
    }

    private fun setupListOfDataIntiRecyclerView() {

        if(getNotesList()?.size!! > 0) {
            recycler_view.visibility = View.VISIBLE

            recycler_view.apply {
                layoutManager = LinearLayoutManager(this@HomeActivity)
                val topSpacingItemDecoration = TopSpacingItemDecoration(30)
                addItemDecoration(topSpacingItemDecoration)
                notesAdapter = RecyclerAdapter(getNotesList())
                adapter = notesAdapter
            }
        } else {
            recycler_view.visibility = View.GONE
        }

    }

    private fun getNotesList(): MutableList<CreatingNewNotes> {
        //creating the instance of DatabaseHandler class
        val dataBaseHandler: DataBaseHandler = DataBaseHandler(this@HomeActivity)
        //calling the viewNotes method of DataBaseHandler

        return dataBaseHandler.viewNotes()
    }

    private fun selectingImage() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItem = arrayOf("Select photo from Gallery", "Capture Photo from Camera" )
        pictureDialog.setItems(pictureDialogItem) { dialog, selection ->
            when(selection) {
                0 -> galleryCheckPermission(this@HomeActivity)
                1 -> cameraCheckPermission(this@HomeActivity)
            }
        }
        pictureDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        setNavigationDrawer()
        return super.onCreateOptionsMenu(menu)
    }

    private fun setNavigationDrawer() {
        toggle = ActionBarDrawerToggle(this, drawerLayout, my_toolbar, R.string.Open, R.string.Close)
        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onTheNaviagtionItemSelected() {
        nav_view.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_notes -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_Container, NotesFragment())
                        .setReorderingAllowed(true).addToBackStack(null).commit()
                    Toast.makeText(applicationContext, "Notes Fragment", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_remainder -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_Container, RemainderFragment())
                        .setReorderingAllowed(true).addToBackStack(null).commit()
                    Toast.makeText(applicationContext, "Remainder Fragment", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_archive -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_Container, ArchiveFragment())
                        .setReorderingAllowed(true).addToBackStack(null).commit()
                    Toast.makeText(applicationContext, "Archive Fragment", Toast.LENGTH_SHORT)
                        .show()
                }
                R.id.nav_deleted -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_Container, DeletedFragment())
                        .setReorderingAllowed(true).addToBackStack(null).commit()
                    Toast.makeText(applicationContext, "Deleted Fragment", Toast.LENGTH_SHORT)
                        .show()
                }

                R.id.nav_logout -> {
                    layOut()
                }

            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

    }



    fun layOut() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_signout_box,null)
        view.dialog_signOut
        builder.setView(view)
        builder.setPositiveButton("Confirm") { _, _ ->
            signOut()
        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.show()

    }

    private fun signOut() {
        mAuth.signOut()
        Toast.makeText(applicationContext, "Logged Out", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun galleryCheckPermission(context: Context) {

        Dexter.withContext(context).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object: PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
               gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(context, "You have denied the storage to select Image",
                    Toast.LENGTH_SHORT).show()
                showRotationalDialogForPermission(context)
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                showRotationalDialogForPermission(context)
            }
        }).onSameThread().check()
    }

    private fun cameraCheckPermission(context: Context) {

        Dexter.withContext(context).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA).withListener(
            object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if(report.areAllPermissionsGranted()) {
                            camera()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRotationalDialogForPermission(context)
                }
            }
        ).onSameThread().check()
    }


    fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)

    }

    fun showRotationalDialogForPermission(context: Context) {
        android.app.AlertDialog.Builder(context).setMessage("It looks like you have turned off Permissions"
                + "required for this feature. It can be enable under App Settings")

            .setPositiveButton("Go To Settings")  { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", "packageName", null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog,_ ->
                dialog.dismiss()
            }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                CAMERA_REQUEST_CODE -> {
                    gettingDataAsBitmap(data)
                    }

                GALLERY_REQUEST_CODE -> { 
                    gettingDataAsBitmap(data)
                }
            }
        }
    }

    private fun gettingDataAsBitmap(data: Intent?) {
        val bitmap =  data?.extras?.get("data") as? Bitmap
        profileImage.setImageBitmap(bitmap)
        if (bitmap != null) {
            handleUpload(bitmap)
        }
    }

    private fun handleUpload(bitmap: Bitmap) {

        val boas = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val reference: StorageReference = FirebaseStorage.getInstance().reference
                                          .child("ProfileImages")
                                          .child("$uid.jpeg")

        reference.putBytes(boas.toByteArray()).addOnSuccessListener {
            getDownloadUrl(reference)
        }
            .addOnFailureListener {
                Log.e(TAG,"OnFailure: ", it)
            }
    }


    private fun getDownloadUrl(reference: StorageReference) {
        reference.downloadUrl.addOnSuccessListener { uri ->
            Log.d("DownloadUrl", "OnSuccess$uri")
            setUserProfileUrl(uri)
        }
    }

    private fun setUserProfileUrl(uri: Uri?) {
        val user = FirebaseAuth.getInstance().currentUser

        val request = UserProfileChangeRequest.Builder().setPhotoUri(uri).build()

        user?.updateProfile(request)?.addOnSuccessListener {
            Toast.makeText(
                this@HomeActivity,
                "Successfully Uploaded",
                Toast.LENGTH_SHORT
            ).show()
        }
            ?.addOnFailureListener {
                Toast.makeText(
                    this@HomeActivity,
                    "Uploading Failed...",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }
}