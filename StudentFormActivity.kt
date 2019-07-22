package com.digimva.epoi

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import java.util.*
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Base64
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import java.text.SimpleDateFormat
import kotlinx.android.synthetic.main.activity_student_form.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

lateinit var student_name_Et: EditText
lateinit var father_name_Et: EditText
lateinit var mother_name_Et: EditText
lateinit var classtecher_name_Et: EditText
lateinit var mobile_no_Et: EditText
lateinit var maleradioBu: RadioButton
lateinit var femaleradioBu: RadioButton
lateinit var other_gender_Bu: RadioButton
lateinit var academiccoordinator_Et: EditText
lateinit var form_submit_Bu: Button
lateinit var principal_name_Et: EditText
lateinit var ChoosePhotoBu: Button

@SuppressLint("StaticFieldLeak")
private var StudentProfimgView : ImageView? = null
private const val IMG_REQUEST = 1
private var bitmap: Bitmap? = null

var methodnewnew = "addstudent"
var genderradio = ""

class StudentFormActivity : AppCompatActivity() {

    var button_date: Button? = null
    var textview_date: TextView? = null
    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_form)

        actionBar?.title = "Fill student details"
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // get the references from layout file
        textview_date = this.text_view_date_1
        button_date = this.button_date_1

        student_name_Et = findViewById(R.id.student_name_et)
        father_name_Et = findViewById(R.id.father_name_et)
        mother_name_Et = findViewById(R.id.mother_name_et)
        classtecher_name_Et = findViewById(R.id.classteacher_name_et)
        mobile_no_Et = findViewById(R.id.mobile_no_et)
        maleradioBu = findViewById(R.id.maleradio_bu)
        femaleradioBu = findViewById(R.id.femaleradio_bu)
        other_gender_Bu= findViewById(R.id.other_gender_radio_bu)
        academiccoordinator_Et = findViewById(R.id.academiccoordinator_et)
        form_submit_Bu = findViewById(R.id.form_submit_bu)
        principal_name_Et = findViewById(R.id.principal_name_et)
        ChoosePhotoBu = findViewById(R.id.choosephotobu)

        StudentProfimgView = findViewById(R.id.studentprofimgview) as ImageView

        ChoosePhotoBu.setOnClickListener {
            selectImage()
        }

        form_submit_Bu.setOnClickListener {
            formsubmit()
        }

        textview_date!!.text = ("YYYY" + "/" + "MM" + "/" + "DD")

        // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        button_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@StudentFormActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMG_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMG_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val path = data.data
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, path)
                StudentProfimgView !!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }


    private fun updateDateInView() {
        val myFormat = "yyyy/MM/dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.getTime())
        var nm = textview_date!!.text.toString()
        birthdate = nm
    }

    private fun formsubmit() {
        var studentname = student_name_Et.text.toString()
        var mothername = mother_name_Et.text.toString()
        var fathername = father_name_Et.text.toString()
        var principalname = principal_name_Et.text.toString()
        var mobile_no = mobile_no_Et.text.toString()
        var academiccoordinatorname = academiccoordinator_Et.text.toString()
        var classteachername = classtecher_name_Et.text.toString()

        if(maleradioBu.isChecked == true) {
            genderradio = "Male"
        }
        if(femaleradioBu.isChecked == true) {
            genderradio = "Female"
        }
        if(other_gender_Bu.isChecked == true) {
            genderradio = "Other"
        }

        /*
        printlnc("method"+ methodnewnew)
        printlnc("uid"+ id)
        printlnc("classname" + "'"+classnameadd+"'")
        printlnc("sectionname" + "'"+sectionname+"'")
        printlnc("name" + "'"+studentname+"'")
        printlnc("mobileno" + "'"+mobile_no+"'")
        printlnc("fathersname" + "'"+fathername+"'")
        printlnc("mothersname" + "'"+mothername+"'")
        printlnc("dob" + "'"+ birthdate+"'")
        printlnc("gender" + "'"+ genderradio+"'")
        printlnc("principal" + "'"+principalname+"'")
        printlnc("academiccoordinator" + "'"+academiccoordinatorname+"'")
        printlnc("classteacher" + "'"+classteachername+"'")
        */
        //printlnc("***************************" + classname)
        //http://192.168.1.8:8080/epoi/getdata.php?method=addclass&uid=1&classname='Class 12'
        var newURL_ROOT = "http://192.168.1.8:8080/epoi/getdata.php"
        var URL_ROOT_PHOTO = "http://192.168.1.8:8080/digimva/epoi/getdataandroid.php"
        //creating volley string request
        val stringRequest = @SuppressLint("ApplySharedPref")
        object : StringRequest(
            Request.Method.POST, newURL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_SHORT).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("method", methodnewnew)
                params.put("uid", id)
                params.put("classname", "'"+classnameadd+"'")
                params.put("sectionname", "'"+sectionname+"'")
                params.put("name", "'"+studentname+"'")
                params.put("mobileno", "'"+mobile_no+"'")
                params.put("fathersname", "'"+fathername+"'")
                params.put("mothersname", "'"+mothername+"'")
                params.put("dob", "'"+ birthdate+"'")
                params.put("gender", "'"+ genderradio+"'")
                params.put("principal", "'"+principalname+"'")
                params.put("academiccoordinator", "'"+academiccoordinatorname+"'")
                params.put("classteacher", "'"+classteachername+"'")
                return params
            }
        }
        //adding request to queue
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        //--------------------------------------------------------------------------------------------------------------

        // Photo Upload Request

        val photouploadstringRequest = object : StringRequest(Request.Method.POST, URL_ROOT_PHOTO,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val Response = jsonObject.getString("response")
                    Toast.makeText(this@StudentFormActivity, "response from server is$Response", Toast.LENGTH_SHORT).show()
                    StudentProfimgView!!.setImageResource(0)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this@StudentFormActivity, "Can not send", Toast.LENGTH_SHORT).show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["name"] = student_name_Et.getText().toString().trim { it <= ' ' }
                params["image"] = imageToString(bitmap!!)
                return params
            }
        }

        MySingleton.getInstance(this@StudentFormActivity).addToRequestQueue(photouploadstringRequest)

    }

    private fun imageToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imgBytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imgBytes, Base64.DEFAULT)
    }

    }