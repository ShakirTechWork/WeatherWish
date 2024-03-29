package com.example.weatherwish.ui.setting

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.weatherwish.Application
import com.example.weatherwish.R
import com.example.weatherwish.broadcastReceivers.WeatherUpdateReceiver
import com.example.weatherwish.constants.AppEnum
import com.example.weatherwish.databinding.FragmentSettingsBinding
import com.example.weatherwish.ui.signIn.SignInActivity
import com.example.weatherwish.utils.Utils
import java.util.Calendar


class SettingsFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = NavHostFragment.findNavController(this@SettingsFragment)
        val repository = (requireActivity().application as Application).appRepository
        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(repository)
        )[SettingsViewModel::class.java]

        attachClickListener()
    }

    private fun attachClickListener() {
        binding.clTopUnitLayout.setOnClickListener {
            if (binding.llBottomTemperatureLayout.isVisible) {
                // The transition of the hiddenView is carried out by the TransitionManager class.
                // Here we use an object of the AutoTransition Class to create a default transition
                binding.llBottomTemperatureLayout.visibility = View.GONE
                TransitionManager.beginDelayedTransition(binding.cdUnitLayout, AutoTransition())
                binding.imgTemperatureArrow.setImageResource(R.drawable.baseline_arrow_down_24)
            } else {
                TransitionManager.beginDelayedTransition(binding.cdUnitLayout, AutoTransition())
                binding.llBottomTemperatureLayout.visibility = View.VISIBLE
                binding.imgTemperatureArrow.setImageResource(R.drawable.baseline_arrow_up_24)
            }
        }

        binding.clMetricLayout.setOnClickListener {
            binding.imgMetricTick.setImageResource(R.drawable.tick_circle)
            binding.imgImperialTick.setImageResource(0)
            settingsViewModel.updateUserUnitPreference("metric")
        }

        binding.clImperialLayout.setOnClickListener {
            binding.imgImperialTick.setImageResource(R.drawable.tick_circle)
            binding.imgMetricTick.setImageResource(0)
            settingsViewModel.updateUserUnitPreference("imperial")
        }

        binding.cdThemeLayout.setOnClickListener {
            Utils.twoOptionAlertDialog(
                requireContext(),
                "Change App Theme",
                "Change app theme to light or dark mode. App will automatically restart in the selected theme.",
                "Light",
                "Dark",
                true,
                {
                    Utils.changeMode(AppEnum.LIGHTMODE)
//                    navController.popBackStack()
//                    requireActivity().finish()
//                    startActivity(Intent(requireActivity(),SplashActivity::class.java))
                },
                {
                    Utils.changeMode(AppEnum.DARKMODE)
                })
        }

        binding.cdSignoutLayout.setOnClickListener {
            Utils.twoOptionAlertDialog(
                requireContext(),
                "Confirmation",
                "Are you sure you want to sign out?",
                "Yes",
                "Cancel",
                true,
                {
                    settingsViewModel.signOutCurrentUser()
                    navController.popBackStack()
                    requireActivity().finish()
                    startActivity(Intent(requireActivity(), SignInActivity::class.java))
                },
                {})
        }

        binding.cdPeriodicLayout.setOnClickListener {
            navController.navigate(R.id.action_settings_to_weather_updates_fragment)
        }

    }

}