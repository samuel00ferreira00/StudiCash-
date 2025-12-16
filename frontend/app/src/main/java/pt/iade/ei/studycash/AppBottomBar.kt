package pt.iade.ei.studycash

import android.app.Activity
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.iade.ei.studycash.ui.theme.StudyCashTheme

@Composable
fun AppBottomBar() {
    val context = LocalContext.current
    val currentActivity = context as? Activity
    val currentClassName = currentActivity?.javaClass?.simpleName ?: ""
    
    val darkTeal = Color(0xFF0E5564)
    val lightTeal = Color(0xFF0E97A4)
    val accentGreen = Color(0xFF10B981)
    
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Barra principal
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = Color.White,
            shadowElevation = 20.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    icon = R.drawable.home,
                    label = "Início",
                    isSelected = currentClassName == "HomeActivity",
                    selectedColor = darkTeal
                ) {
                    if (currentClassName != "HomeActivity") {
                        context.startActivity(Intent(context, HomeActivity::class.java))
                    }
                }
                
                BottomNavItem(
                    icon = R.drawable.lock,
                    label = "Transações",
                    isSelected = currentClassName == "TransactionsActivity",
                    selectedColor = darkTeal
                ) {
                    if (currentClassName != "TransactionsActivity") {
                        context.startActivity(Intent(context, TransactionsActivity::class.java))
                    }
                }
                
                // Botão central flutuante (+)
                Box(modifier = Modifier.size(48.dp)) // Espaço reservado
                
                BottomNavItem(
                    icon = R.drawable.piechart,
                    label = "Orçamento",
                    isSelected = currentClassName == "BudgetActivity",
                    selectedColor = darkTeal
                ) {
                    if (currentClassName != "BudgetActivity") {
                        context.startActivity(Intent(context, BudgetActivity::class.java))
                    }
                }
                
                BottomNavItem(
                    icon = R.drawable.target,
                    label = "Metas",
                    isSelected = currentClassName == "GoalsActivity",
                    selectedColor = darkTeal
                ) {
                    if (currentClassName != "GoalsActivity") {
                        context.startActivity(Intent(context, GoalsActivity::class.java))
                    }
                }
            }
        }
        
        // Botão flutuante central (+)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
                .shadow(12.dp, CircleShape)
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(lightTeal, darkTeal)
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    context.startActivity(Intent(context, NewRevenueActivity::class.java))
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    @DrawableRes icon: Int,
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor else Color(0xFF9CA3AF),
        label = "color"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor else Color(0xFF9CA3AF),
        label = "textColor"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .scale(scale)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isSelected) selectedColor.copy(alpha = 0.1f) else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.size(22.dp),
                colorFilter = ColorFilter.tint(iconColor)
            )
        }
        
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor
        )
        
        // Indicador de seleção
        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(width = 20.dp, height = 3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(selectedColor)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomBarPreview() {
    StudyCashTheme {
        AppBottomBar()
    }
}
