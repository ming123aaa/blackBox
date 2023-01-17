package top.niunaijun.blackboxa.view.fake

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.util.GeoPoint
import top.niunaijun.blackbox.entity.location.BLocation
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.view.fake.util.Gps
import top.niunaijun.blackboxa.view.fake.util.PositionUtil


class SetFakeLocationActivity: Activity() {
    val tv_location: TextView by lazy {
        findViewById(R.id.tv_location)
    }
    val edit_latitude: TextView by lazy {
        findViewById(R.id.edit_GPSLatitude)
    }
    val edit_longitude: TextView by lazy {
        findViewById(R.id.edit_GPSLongitude)
    }
    val btn_save: TextView by lazy {
        findViewById(R.id.btn_save)
    }
    val radio_earth: RadioButton by lazy {
        findViewById(R.id.radio_earth)
    }
    val radio_mars: RadioButton by lazy {
        findViewById(R.id.radio_Mars)
    }
    val radio_baidu: RadioButton by lazy {
        findViewById(R.id.radio_bd09)
    }

    val btn_getLocation: Button by lazy {
        findViewById(R.id.btn_getLocation)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_fake_location)
        val location: BLocation? = intent.getParcelableExtra("location")
        location?.let {
            edit_longitude.text = ""+it.longitude
            edit_latitude.text = ""+it.latitude
            tv_location.text = "经度:${it.longitude} 纬度:${it.latitude} \n 注意:有些app会使用蓝牙定位,可能需要关闭蓝牙"
        }

        btn_save.setOnClickListener {
            var longitudeString = edit_longitude.text.toString()
            var latitudeString = edit_latitude.text.toString()
            if (longitudeString.isEmpty() || latitudeString.isEmpty()) {
                Toast.makeText(this@SetFakeLocationActivity, "请输入", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val context: Context = this@SetFakeLocationActivity

            var latitude = latitudeString.toDouble()
            var longitude = longitudeString.toDouble()
            var gps = Gps(latitude, longitude)
            if (radio_mars.isChecked) {
                gps = PositionUtil.gcj_To_Gps84(latitude, longitude)//火星坐标系

            } else if (radio_earth.isChecked) {//地球坐标系

            } else if (radio_baidu.isChecked) {//百度坐标系
                gps = PositionUtil.bd09_To_Gps84(latitude, longitude)

            }

            latitude = gps.wgLat
            longitude = gps.wgLon


            finishWithResult(GeoPoint(latitude,longitude))

        }
        btn_getLocation.setOnClickListener {
            var s = "https://api.map.baidu.com/lbsapi/getpoint/index.html?qq-pf-to=pcqq.group"
            var parse = Uri.parse(s)
            var intent = Intent()
            intent.setAction("android.intent.action.VIEW")
            intent.setData(parse)
            startActivity(intent)
        }
    }



    private fun finishWithResult(geoPoint: GeoPoint) {
        intent.putExtra("latitude", geoPoint.latitude)
        intent.putExtra("longitude", geoPoint.longitude)
        setResult(Activity.RESULT_OK, intent)
        val imm: InputMethodManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        window.peekDecorView()?.run {
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
        finish()
    }
}