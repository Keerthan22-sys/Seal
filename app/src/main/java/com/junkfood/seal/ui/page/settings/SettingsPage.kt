package com.junkfood.seal.ui.page.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Aod
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.rounded.EnergySavingsLeaf
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.junkfood.seal.App
import com.junkfood.seal.R
import com.junkfood.seal.ui.common.LocalDarkTheme
import com.junkfood.seal.ui.common.Route
import com.junkfood.seal.ui.component.BackButton
import com.junkfood.seal.ui.component.PreferencesHintCard
import com.junkfood.seal.ui.component.SettingItem
import com.junkfood.seal.util.PreferenceUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(navController: NavController) {
    val context = LocalContext.current
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    var showBatteryHint by remember { mutableStateOf(!pm.isIgnoringBatteryOptimizations(context.packageName)) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            showBatteryHint = !pm.isIgnoringBatteryOptimizations(context.packageName)
        }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val fraction = CubicBezierEasing(1f,0f,.8f,.4f).transform(scrollBehavior.state.overlappedFraction)

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(title = {
                Text(
                    stringResource(R.string.settings),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = fraction)
                )
            },
                navigationIcon = { BackButton(modifier = Modifier.padding(start = 8.dp)) { navController.popBackStack() } },
                scrollBehavior = scrollBehavior
            )
        }) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            item {
                Text(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .padding(24.dp),
                    text = stringResource(id = R.string.settings),
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            item {
                AnimatedVisibility(
                    visible = showBatteryHint, exit = shrinkVertically() + fadeOut()
                ) {
                    PreferencesHintCard(
                        title = stringResource(R.string.battery_configuration),
                        icon = Icons.Rounded.EnergySavingsLeaf,
                        description = stringResource(R.string.battery_configuration_desc),
                        isDarkTheme = LocalDarkTheme.current.isDarkTheme()
                    ) {
                        launcher.launch(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                            data = Uri.parse("package:${context.packageName}")
                        })
                        showBatteryHint = !pm.isIgnoringBatteryOptimizations(context.packageName)
                    }
                }
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.general_settings),
                    description = stringResource(
                        id = R.string.general_settings_desc
                    ),
                    icon = Icons.Filled.SettingsApplications
                ) {
                    navController.navigate(Route.GENERAL_DOWNLOAD_PREFERENCES) {
                        launchSingleTop = true
                    }
                }
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.download_directory),
                    description = stringResource(
                        id = R.string.download_directory_desc
                    ),
                    icon = Icons.Filled.Folder
                ) {
                    navController.navigate(Route.DOWNLOAD_DIRECTORY) {
                        launchSingleTop = true
                    }
                }
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.format),
                    description = stringResource(id = R.string.format_settings_desc),
                    icon = if (PreferenceUtil.getValue(PreferenceUtil.EXTRACT_AUDIO)) Icons.Filled.AudioFile else Icons.Filled.VideoFile
                ) {
                    navController.navigate(Route.DOWNLOAD_FORMAT) {
                        launchSingleTop = true
                    }
                }
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.network),
                    description = stringResource(id = R.string.network_settings_desc),
                    icon = if (App.connectivityManager.isActiveNetworkMetered) Icons.Filled.SignalCellular4Bar else Icons.Filled.SignalWifi4Bar
                ) {
                    navController.navigate(Route.NETWORK_PREFERENCES) {
                        launchSingleTop = true
                    }
                }
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.custom_command),
                    description = stringResource(id = R.string.custom_command_desc),
                    icon = Icons.Outlined.Terminal
                ) {
                    navController.navigate(Route.TEMPLATE) {
                        launchSingleTop = true
                    }
                }
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.display), description = stringResource(
                        id = R.string.display_settings
                    ), icon = Icons.Filled.Aod
                ) {
                    navController.navigate(Route.APPEARANCE) { launchSingleTop = true }
                }
            }
            item {

                SettingItem(
                    title = stringResource(id = R.string.about), description = stringResource(
                        id = R.string.about_page
                    ), icon = Icons.Filled.Info
                ) {
                    navController.navigate(Route.ABOUT) { launchSingleTop = true }
                }
            }
        }
    }
}