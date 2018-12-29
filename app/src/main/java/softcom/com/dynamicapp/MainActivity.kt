package softcom.com.dynamicapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import softcom.com.dynamicapp.ui.fragments.DynamicFragment

class MainActivity : AppCompatActivity(),
    DynamicFragment.OnFragmentInterractionListener {
    override fun onNextClicked(pageNumber: Int) {

    }

    override fun onCompleteClicked(data: String) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DynamicFragment.newInstance(""))
                .commitNow()
        }
    }

}
