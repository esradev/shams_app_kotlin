package ir.wpstorm.shams.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.wpstorm.shams.R
import ir.wpstorm.shams.ui.theme.Emerald400
import ir.wpstorm.shams.ui.theme.Emerald50
import ir.wpstorm.shams.ui.theme.Emerald700
import ir.wpstorm.shams.ui.theme.Gray400
import ir.wpstorm.shams.ui.theme.Gray500
import ir.wpstorm.shams.ui.theme.Gray700
import ir.wpstorm.shams.viewmodel.CategoryItem

@Composable
fun CourseCard(
    course: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Teacher Info Row at the top
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "آیت الله حسینی آملی (حفظه الله)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Right
                        )

                        // Category Badge
                        Row(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .background(
                                    color = if (MaterialTheme.colorScheme.surface == Color.White) {
                                        Emerald50
                                    } else {
                                        Emerald700.copy(alpha = 0.3f)
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "درس خارج",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                color = if (MaterialTheme.colorScheme.surface == Color.White) {
                                    Emerald700
                                } else {
                                    Emerald400
                                }
                            )
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "درس خارج",
                                modifier = Modifier.size(12.dp),
                                tint = if (MaterialTheme.colorScheme.surface == Color.White) {
                                    Emerald700
                                } else {
                                    Emerald400
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Teacher Avatar using local drawable
                    Image(
                        painter = painterResource(id = R.drawable.ic_teacher_avatar),
                        contentDescription = "آیت الله حسینی آملی",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Course Title
                Text(
                    text = course.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Right,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                // Course Description
                if (course.description.isNotEmpty()) {
                    val cleanDescription = course.description
                        .replace(Regex("<[^>]+>"), "")
                        .trim()
                        .take(120) + if (course.description.length > 120) "..." else ""

                    Text(
                        text = cleanDescription,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        ),
                        color = Gray500,
                        textAlign = TextAlign.Right,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Duration in hours
                    val totalMinutes = course.count * 30
                    val hours = totalMinutes / 60
                    val minutes = totalMinutes % 60

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (minutes > 0) "$hours:${minutes.toString().padStart(2, '0')} ساعت" else "$hours ساعت",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Gray700
                        )
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "مدت زمان",
                            modifier = Modifier.size(18.dp),
                            tint = Emerald700
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Sessions count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${course.count} جلسه",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Gray700
                        )
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "تعداد جلسات",
                            modifier = Modifier.size(18.dp),
                            tint = Emerald700
                        )
                    }
                }
            }
        }
    }
}
