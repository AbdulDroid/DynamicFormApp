package softcom.com.dynamicapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import softcom.com.dynamicapp.R

class DynamicFragment : Fragment() {

    companion object {
        fun newInstance(data: String) = DynamicFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    private var listener: OnFragmentInterractionListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dynamic_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInterractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInterractionListener{
        fun onNextClicked(pageNumber: Int)
        fun onCompleteClicked(data: String)
    }

}
