package com.bitla.ts.presentation.components

import android.content.Context
import androidx.annotation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import androidx.constraintlayout.compose.*
import com.bitla.ts.R
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.ResourceProvider
import com.bitla.ts.utils.common.appTextSize

var paddingValues: Dp = 0.dp
var spaceBetweenTextField: Dp = 2.dp
val tickIcon = Icons.Filled.Check
val crossIcon = Icons.Filled.Clear

// EditText/TextField
@Composable
fun TextFieldComponent(
    modifier: Modifier,
    value: String,
    label: String,
    placeholder: String? = "",
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    isEnable: Boolean? = null,
    isError: Boolean? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly : Boolean? = null,
    context:Context?= null
) {
    
    TextField(
        readOnly = readOnly ?: false,
        isError = isError ?: false,
        value = value,
        onValueChange = onValueChange,
        enabled = isEnable ?: true,
        modifier = modifier,
        textStyle = TextStyle(
            color = colorResource(id = R.color.colorBlackShadow),
            fontFamily = FontFamily(Font(R.font.notosans_regular)),
            fontSize = appTextSize(context,12).sp
        ),
        keyboardOptions = keyboardOptions,
        label = {
            TextNormalSmall(
                text = label,
                modifier = Modifier,
                textStyle = TextStyle(
                    colorResource(id = R.color.colorBlackShadow),
                    fontFamily = FontFamily(Font(R.font.notosans_regular))
                ),
            )
        },
//        placeholder = {
//            TextNormalRegular(
//                text = placeholder?:"",
//                modifier = Modifier
//            )
//        },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = colorResource(id = R.color.colorAccent),
            cursorColor = Color.Gray
        ),
        trailingIcon = trailingIcon
    )
}

@Composable
fun BasicTextFieldComponentRounded(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    isEnable: Boolean? = null,
    isError: Boolean? = null,
//        label: String? = "",
    placeholder: String? = "",
    readOnly: Boolean? = null,
    textStyle: TextStyle? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    cursorBrush : Brush? = null,
) {

    BasicTextField (
        value = value,
        onValueChange = {
            onValueChange.invoke(it)
        }, enabled = isEnable ?: true,
        modifier = modifier
            .focusable(true)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(4.dp)
            )
            .height(40.dp),
        textStyle = textStyle ?: TextStyle(
            color = colorResource(id = R.color.colorBlackShadow),
            fontFamily = FontFamily(Font(R.font.notosans_regular)),
            fontSize = 12.sp
        ), keyboardOptions = keyboardOptions,
        readOnly = readOnly ?: false,
        cursorBrush = cursorBrush ?:SolidColor(Color.Black),

        decorationBox = {
            Box(
                Modifier.padding(start = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f)) {
                        it()
                    }
                    trailingIcon?.invoke()
                }
            }
        }
    )
}

@Composable
fun BasicFareTextFieldComponentRounded(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    isEnable: Boolean? = null,
    isError: Boolean? = null,
//        label: String? = "",
    placeholder: String? = "",
    readOnly: Boolean? = null,
    textStyle: TextStyle? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    cursorBrush : Brush? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {

    BasicTextField (
        value = value,
        onValueChange = {
            onValueChange.invoke(it)
        }, enabled = isEnable ?: true,
        modifier = modifier
            .focusable(true)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(4.dp)
            )
            .height(40.dp),
        textStyle = textStyle ?: TextStyle(
            color = colorResource(id = R.color.colorBlackShadow),
            fontFamily = FontFamily(Font(R.font.notosans_regular)),
            fontSize = 12.sp
        ), keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        readOnly = readOnly ?: false,
        cursorBrush = cursorBrush ?:SolidColor(Color.Black),

        decorationBox = {
            Box(
                Modifier.padding(start = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f)) {
                        it()
                    }
                    trailingIcon?.invoke()
                }
            }
        }
    )
}

@Composable
fun TextFieldComponentRounded(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    isEnable: Boolean? = null,
    isError: Boolean? = null,
    label: String?=null,
    placeholder: String? = "",
    readOnly :Boolean?=null,
    textStyle: TextStyle?=null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation : VisualTransformation? = null
) {

    var dropDownWidth by remember { mutableIntStateOf(0) }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextField(
        value = value,
        enabled = isEnable ?: true,
        isError = isError ?: false,
        interactionSource = interactionSource,
        onValueChange = {
            onValueChange.invoke(it)
        },
        modifier = modifier
            .defaultMinSize(minHeight = 12.dp)
            .onSizeChanged {
                dropDownWidth = it.width
            },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = colorResource(id = R.color.colorDimShadow6),
            unfocusedBorderColor = colorResource(id = R.color.colorDimShadow6)
        ),
        label = {
            if (label != null) {
                TextBoldSmall(
                    text = label,
                    modifier = Modifier,
                    textAlign = TextAlign.Start,
                    style = textStyle ?: TextStyle(
                        color = colorResource(id = R.color.colorBlackShadow),
                        fontSize = 12.sp
                    )
                )
            }
        },
        trailingIcon = trailingIcon,
        textStyle = textStyle ?: TextStyle(
            color = colorResource(id = R.color.colorBlackShadow),
            fontFamily = FontFamily(Font(R.font.notosans_regular)),
            fontSize = 13.sp
        ),
        keyboardOptions = keyboardOptions,
        readOnly = readOnly ?: false,
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        ),
        visualTransformation = visualTransformation ?: VisualTransformation.None,
    )
}

@Composable
fun TextBoldRegular(text: String, modifier: Modifier, textStyle: TextStyle? = null) {
    if (textStyle != null) {
        Text(
            text = text,
            modifier = modifier,
            style = textStyle,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.notosans_bold)),
        )
    }
}

@Composable
fun TextBoldSmall(
    text: String,
    modifier: Modifier,
    style: TextStyle? = null,
    textAlign: TextAlign,
) {
    Text(
        text = text,
        modifier = modifier,
        style = style ?: TextStyle(
            color = colorResource(id = R.color.colorBlackShadow),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = textAlign,
            fontFamily = FontFamily(Font(R.font.notosans_bold))
        ),
    )
}

@Composable
fun TextBoldLarge(text: String, modifier: Modifier, style: TextStyle? = null) {
    Text(
        text = text,
        modifier = modifier,
        style = style ?: TextStyle(
            color = colorResource(id = R.color.colorBlackShadow),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            fontFamily = FontFamily(Font(R.font.notosans_bold))
        )
    )
}


// TextView with Normal Text
@Composable
fun TextNormalRegular(
    text: String,
    modifier: Modifier,
    textStyle: TextStyle? = null,
) {
    Text(
        text = text,
        modifier = modifier,
        style = textStyle ?: TextStyle(
            color = colorResource(id = R.color.colorBlackShadow),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily(Font(R.font.notosans_regular))
        )
    )
}

@Composable
fun TextNormalSmall(
    text: String,
    modifier: Modifier,
    textStyle: TextStyle? = null,
) {
    Text(
        text = text,
        modifier = modifier,
        style = textStyle ?: TextStyle(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = FontFamily(Font(R.font.notosans_regular))
    )
}

@Composable
fun TextNormalLarge(text: String, modifier: Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            color = colorResource(id = R.color.colorBlackShadow),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily(Font(R.font.notosans_regular))
        )
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardComponent(
    shape: Shape,
    bgColor: Color,
    modifier: Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Card(elevation = 1.dp,
        shape = shape,
        backgroundColor = bgColor,
        modifier = modifier, onClick = { onClick() }) {
        content.invoke()
    }
}

@Composable
fun CreateButton(
    textColor: Color? = null,
    colors: ButtonColors? = null,
    style: TextStyle? = null,
    modifier: Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean? = null,
) {
    Button(
        modifier = modifier.padding(0.dp),
        onClick = onClick,
        enabled = enabled ?: true,
        //colors = colors ?: ButtonDefaults.buttonColors(backgroundColor = Color(0XFF00ADB5))
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        elevation = ButtonDefaults.elevation(0.dp, 0.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier,
            style = style ?: TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.notosans_regular))
            ),
            color = textColor ?: colorResource(id = R.color.white)
        )
    }
}

@Composable
fun DividerLine(modifier: Modifier? = null) {
    Divider(
        color = colorResource(id = R.color.view_color),
        thickness = 1.dp,
        modifier = modifier ?: Modifier,
    )
}

@Composable
fun SpaceComponent(modifier: Modifier) {
    Spacer(modifier = modifier)
}


@Composable
fun TextMultiStyle(originalText: String, boldText: String, normalText: String) {
    val startIndex = originalText.indexOf(normalText)
    val endIndex = startIndex.plus(normalText.length)
    Text(buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp)) {
            append(boldText)
        }
        append(normalText)
        addStyle(
            style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
            startIndex,
            endIndex
        )
    })
}

@Composable
fun CheckBoxComponent(checkBoxText: String, isChecked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = isChecked, onCheckedChange = onChecked)
        TextNormalSmall(text = checkBoxText, modifier = Modifier.padding(start = 2.dp))
    }
}

@Composable
fun RetrievePaxDialog(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    onCheckedChange: (Int, Boolean) -> Unit,
    onDismiss: () -> Unit,
    onUpdatePax: () -> Unit,
) {
    
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = false, dismissOnClickOutside = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                
                TextBoldSmall(
                    text = stringResource(id = R.string.passengers).uppercase(),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 16.dp, top = 16.dp),
                    textAlign = TextAlign.Start
                )
                
                DividerLine(modifier = Modifier.padding(top = 8.dp))
                
                LazyColumn(state = rememberLazyListState(), modifier = Modifier.weight(.92f))
                {
                    itemsIndexed(passengerDetailsViewModel.passengerHistoryList) { index, item ->
                        var checked by rememberSaveable {
                            mutableStateOf(false)
                        }
                        Row(
                            Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    checked =
                                        if (passengerDetailsViewModel.checkedPassengerList.size == passengerDetailsViewModel.passengerDataList.size) {
                                            false
                                        } else {
                                            it
                                        }
                                    onCheckedChange(index, it)
                                }
                            )
                            
                            TextBoldSmall(
                                text = item.name,
                                modifier = Modifier.align(Alignment.CenterVertically),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
                
                Column(
                    modifier = Modifier
                        .weight(0.08f)
                ) {
                    DividerLine(modifier = Modifier.padding(start = 16.dp, end = 16.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(top = 8.dp)
                    ) {
                        
                        CreateButton(
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.button_light_color).copy(
                                    alpha = 1f
                                )
                            ),
                            modifier = Modifier
                                .height(44.dp)
                                .background(colorResource(id = R.color.button_light_color))
                                .align(Alignment.Bottom)
                                .weight(1F),
                            text = stringResource(id = R.string.goBack),
                            onClick = {
                                onDismiss()
                                passengerDetailsViewModel.showDialog.value = false
                                passengerDetailsViewModel.isRetrieveClicked.value = false
                            },
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.notosans_bold)),
                                fontSize = 14.sp
                            ),
                            textColor = colorResource(id = R.color.colorPrimary)
                        
                        )
                        
                        CreateButton(
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.colorPrimary).copy(
                                    alpha = 1f
                                )
                            ),
                            modifier = Modifier
                                .height(44.dp)
                                .background(colorResource(id = R.color.colorPrimary))
                                .weight(1F),
                            text = stringResource(id = R.string.update),
                            onClick = {
                                onUpdatePax()
                            },
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.notosans_bold)),
                                fontSize = 14.sp
                            )
                        )
                    }
                }
                
                
            }
        }
    }
}

@Composable
fun EditButton(
    borderStroke: BorderStroke,
    onClick: () -> Unit,
) {
    
    Box(
        modifier = Modifier
            .absoluteOffset((12).dp, 0.dp)
            .fillMaxSize()
            .padding(top = 12.dp),
        Alignment.CenterEnd
    ) {
        
        Row {
            OutlinedButton(
                border = borderStroke,
                modifier = Modifier
                    .width(62.dp)
                    .padding(0.dp)
                    .height(30.dp)
                    .padding(end = 4.dp),
                onClick = onClick
            ) {
                TextBoldSmall(
                    text = stringResource(id = R.string.edit),
                    modifier = Modifier.padding(bottom = 0.dp),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        color = colorResource(id = R.color.colorPrimary),
                        fontFamily = FontFamily(Font(R.font.notosans_regular)),
                        fontSize = 12.sp
                    )
                )
            }
            TickIcon()
        }
    }
}

@Composable
fun TickIcon() {
    Icon(
        imageVector = tickIcon,
        "",
        tint = colorResource(id = R.color.colorPrimary),
        modifier = Modifier
            .padding(end = 8.dp)
            .padding(top = 4.dp)
    )
}

@Composable
fun CrossIcon() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp),
        Alignment.CenterEnd
    ) {
        Icon(
            imageVector = crossIcon,
            "",
            tint = colorResource(id = R.color.colorRed2)
        )
    }
}

fun isEnableVIPTicket(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {
    
    passengerDetailsViewModel.apply {
        if (passengerDetailsViewModel.isVIPTicketChecked.value) {
            isCouponCodeChecked.value = false
            isPrePostponeTicketChecked.value = false
            isPrivilegeCardChecked.value = false
            isApplySmartMilesChecked.value = false
            isDiscountAmountChecked.value = false
            isQuotePreviousPNRChecked.value = false
            isGSTDetailsChecked.value = false
//            isFreeTicketChecked.value = false
            isExposedVIPTicketDropdown.value = false
            isEditButtonVisible.value = false
            isCouponCodeEnable.value = false
            isPromotionCouponEnable.value = false
            isPrePostponeTicketEnable.value = false
            isPrivilegeCardEnable.value = false
            isApplySmartMilesEnable.value = false
            isDiscountAmountEnable.value = false
            isQuotePreviousPNREnable.value = false
            isGSTDetailsEnable.value = false
//            isFreeTicketEnable.value = false
            isExposedVIPTicketDropdown.value = false
            if(isPromotionCouponChecked.value){
                isPromotionCouponChecked.value = false
                removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.promotion_coupon))

            }
        } else {
            isCouponCodeEnable.value = true
            isPromotionCouponEnable.value = true
            isPrePostponeTicketEnable.value = true
            isPrivilegeCardEnable.value = true
            isApplySmartMilesEnable.value = true
            isDiscountAmountEnable.value = true
            isQuotePreviousPNREnable.value = true
            isGSTDetailsEnable.value = true
//            isFreeTicketEnable.value = true
            
            
        }
    }
}

fun isEnableFreeTicket(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {
    
    passengerDetailsViewModel.apply {
        if (passengerDetailsViewModel.isFreeTicketChecked.value) {
            isCouponCodeChecked.value = false
            isPrePostponeTicketChecked.value = false
            isPrivilegeCardChecked.value = false
            isApplySmartMilesChecked.value = false
            isDiscountAmountChecked.value = false
            isQuotePreviousPNRChecked.value = false
            isGSTDetailsChecked.value = false
//            isVIPTicketChecked.value = false
            isExposedVIPTicketDropdown.value = false
            isEditButtonVisible.value = false
            if(isPromotionCouponChecked.value){
                isPromotionCouponChecked.value = false
                removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.promotion_coupon))
            }
            isCouponCodeEnable.value = false
            isPromotionCouponEnable.value = false
            isPrePostponeTicketEnable.value = false
            isPrivilegeCardEnable.value = false
            isApplySmartMilesEnable.value = false
            isDiscountAmountEnable.value = false
            isQuotePreviousPNREnable.value = false
            isGSTDetailsEnable.value = false
//            isVIPTicketEnable.value = false
            isExposedVIPTicketDropdown.value = false
        } else {
            isCouponCodeEnable.value = true
            isPromotionCouponEnable.value = true
            isPrePostponeTicketEnable.value = true
            isPrivilegeCardEnable.value = true
            isApplySmartMilesEnable.value = true
            isDiscountAmountEnable.value = true
            isQuotePreviousPNREnable.value = true
            isGSTDetailsEnable.value = true
//            isVIPTicketEnable.value = true
        }
        
    }
}

fun Modifier.bottomBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }
        
        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx / 2
            
            drawLine(
                color = color,
                start = Offset(x = 0f, y = height),
                end = Offset(x = width, y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        )
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFD5D1D1),
                Color(0xFFDDDBDB),
                Color(0xFFE9E8E8),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}

@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        )
        
        val transition = rememberInfiniteTransition()
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(800), repeatMode = RepeatMode.Reverse
            )
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

@Composable
fun DrawableWrapper(
    modifier: Modifier = Modifier,
    @DrawableRes drawableStart: Int? = null,
    content: @Composable () -> Unit,
) {
    ConstraintLayout(modifier) {
        val (refImgStart, refContent) = createRefs()
        Box(Modifier.constrainAs(refContent) {
            start.linkTo(drawableStart?.let { refImgStart.end } ?: parent.start)
        }) {
            content()
        }
        
        drawableStart?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = null,
                Modifier.constrainAs(refImgStart) {
                    start.linkTo(parent.start)
                }
            )
        }
        
    }
}

