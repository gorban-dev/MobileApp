package ru.gorban.mobileinvestapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.gorban.mobileinvestapp.databinding.FragmentForecastBinding


/**
 * A simple [Fragment] subclass.
 * Use the [ForecastFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForecastFragment : Fragment() {

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForecastBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    companion object {
        fun getInstance() = ForecastFragment()
    }
}