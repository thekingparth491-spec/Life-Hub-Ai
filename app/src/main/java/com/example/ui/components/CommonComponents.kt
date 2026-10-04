package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.model.Tool

object IconHelper {
    fun getIcon(name: String): ImageVector {
        return when (name) {
            "calculate" -> Icons.Default.Calculate
            "widgets" -> Icons.Default.Widgets
            "sync_alt" -> Icons.Default.SyncAlt
            "cake" -> Icons.Default.Cake
            "date_range" -> Icons.Default.DateRange
            "receipt_long" -> Icons.Default.ReceiptLong
            "account_balance" -> Icons.Default.AccountBalance
            "percent" -> Icons.Default.Percent
            "local_dining" -> Icons.Default.LocalDining
            "schedule" -> Icons.Default.Schedule
            "payments", "receipt" -> Icons.Default.Payments
            "pie_chart" -> Icons.Default.PieChart
            "notifications_active" -> Icons.Default.NotificationsActive
            "credit_card" -> Icons.Default.CreditCard
            "autorenew" -> Icons.Default.Autorenew
            "savings" -> Icons.Default.Savings
            "call_split" -> Icons.Default.CallSplit
            "monetization_on" -> Icons.Default.MonetizationOn
            "trending_up" -> Icons.Default.TrendingUp
            "account_balance_wallet" -> Icons.Default.AccountBalanceWallet
            "smartphone", "sd_storage" -> Icons.Default.SdStorage
            "photo_library" -> Icons.Default.PhotoLibrary
            "folder_copy" -> Icons.Default.FolderCopy
            "battery_charging_full" -> Icons.Default.BatteryChargingFull
            "bolt" -> Icons.Default.Bolt
            "apps" -> Icons.Default.Apps
            "speed" -> Icons.Default.Speed
            "wifi" -> Icons.Default.Wifi
            "qr_code_scanner" -> Icons.Default.QrCodeScanner
            "content_paste" -> Icons.Default.ContentPaste
            "auto_awesome" -> Icons.Default.AutoAwesome
            "mic" -> Icons.Default.Mic
            "groups" -> Icons.Default.Groups
            "picture_as_pdf" -> Icons.Default.PictureAsPdf
            "menu_book" -> Icons.Default.MenuBook
            "email" -> Icons.Default.Email
            "chat" -> Icons.Default.Chat
            "spellcheck" -> Icons.Default.Spellcheck
            "badge" -> Icons.Default.Badge
            "photo_camera" -> Icons.Default.PhotoCamera
            "smart_toy" -> Icons.Default.SmartToy
            "school", "assignment" -> Icons.Default.Assignment
            "alarm" -> Icons.Default.Alarm
            "task_alt" -> Icons.Default.TaskAlt
            "style" -> Icons.Default.Style
            "timer" -> Icons.Default.Timer
            "calendar_view_week" -> Icons.Default.CalendarViewWeek
            "fact_check" -> Icons.Default.FactCheck
            "grade" -> Icons.Default.Grade
            "note_alt" -> Icons.Default.NoteAlt
            "description" -> Icons.Default.Description
            "home", "family_restroom" -> Icons.Default.FamilyRestroom
            "shopping_cart" -> Icons.Default.ShoppingCart
            "inventory_2" -> Icons.Default.Inventory2
            "build" -> Icons.Default.Build
            "verified" -> Icons.Default.Verified
            "electric_bolt" -> Icons.Default.ElectricBolt
            "water_drop" -> Icons.Default.WaterDrop
            "event" -> Icons.Default.Event
            "checklist" -> Icons.Default.Checklist
            "folder_shared" -> Icons.Default.FolderShared
            "directions_car", "car_repair" -> Icons.Default.DirectionsCar
            "local_gas_station" -> Icons.Default.LocalGasStation
            "policy" -> Icons.Default.Policy
            "eco" -> Icons.Default.Eco
            "security" -> Icons.Default.Security
            "two_wheeler" -> Icons.Default.TwoWheeler
            "commute" -> Icons.Default.Commute
            "insights" -> Icons.Default.Insights
            "favorite", "local_drink" -> Icons.Default.LocalDrink
            "bedtime" -> Icons.Default.Bedtime
            "directions_walk" -> Icons.Default.DirectionsWalk
            "check_circle" -> Icons.Default.CheckCircle
            "self_improvement" -> Icons.Default.SelfImprovement
            "fitness_center" -> Icons.Default.FitnessCenter
            "restaurant" -> Icons.Default.Restaurant
            "medication" -> Icons.Default.Medication
            "medical_services" -> Icons.Default.MedicalServices
            "health_and_safety" -> Icons.Default.HealthAndSafety
            "sos" -> Icons.Default.Warning
            "location_on" -> Icons.Default.LocationOn
            "contact_phone" -> Icons.Default.ContactPhone
            "lock" -> Icons.Default.Lock
            "key" -> Icons.Default.Key
            "hourglass_top" -> Icons.Default.HourglassTop
            "phonelink_lock" -> Icons.Default.PhonelinkLock
            "phone_in_talk" -> Icons.Default.PhoneInTalk
            "report_problem" -> Icons.Default.ReportProblem
            "palette", "format_quote" -> Icons.Default.FormatQuote
            "tag" -> Icons.Default.Tag
            "auto_stories" -> Icons.Default.AutoStories
            "format_paint" -> Icons.Default.FormatPaint
            "celebration" -> Icons.Default.Celebration
            "festival" -> Icons.Default.Festival
            "grid_view" -> Icons.Default.GridView
            "videocam" -> Icons.Default.Videocam
            "record_voice_over" -> Icons.Default.RecordVoiceOver
            "stars" -> Icons.Default.Stars
            else -> Icons.Default.Handyman
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeHubTopAppBar(
    title: String,
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun ToolCard(
    tool: Tool,
    onToolClick: (Tool) -> Unit,
    onFavoriteToggle: (Tool) -> Unit,
    modifier: Modifier = Modifier
) {
    val favColor by animateColorAsState(
        targetValue = if (tool.isFavorite) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        label = "fav_color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToolClick(tool) }
            .testTag("tool_card_${tool.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(46.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = IconHelper.getIcon(tool.iconName),
                        contentDescription = tool.name,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tool.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = { onFavoriteToggle(tool) },
                modifier = Modifier
                    .size(44.dp)
                    .testTag("favorite_button_${tool.id}")
            ) {
                Icon(
                    imageVector = if (tool.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (tool.isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = favColor
                )
            }
        }
    }
}

@Composable
fun QuickActionChip(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("quick_action_$title"),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
