package no.martin.app.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import no.martin.app.R
import no.martin.app.view.model.Ad
import no.martin.app.view.model.Resource
import no.martin.app.view.viewModel.AdViewModel
import no.martin.app.view.viewModel.Filter

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<AdViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val adsResult = viewModel.ads.collectAsState(Resource.Loading)
            val filter = viewModel.filter.collectAsState()
            when (val result = adsResult.value) {
                is Resource.Success -> {
                    AdsSuccess(
                        ads = result.value,
                        filter = filter.value,
                        favoriteClicked = { viewModel.toggleFavorite(it) },
                        toggleFavoriteFilter = { viewModel.toggleFavoriteFilter() }
                    )
                }
                is Resource.Error -> AdsError(retryClicked = { viewModel.refreshAds() })
                is Resource.Loading -> AdsLoading()
            }
        }
    }
}

@Composable
private fun AdsSuccess(
    ads: List<Ad>,
    filter: Filter,
    favoriteClicked: (Ad) -> Unit,
    toggleFavoriteFilter: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
    ) {
        AdList(
            ads = ads,
            favoriteClicked = favoriteClicked
        )
        IconButton(
            onClick = toggleFavoriteFilter,
            modifier = Modifier
                .padding(
                    end = 16.dp,
                    bottom = 16.dp
                )
                .size(48.dp)
                .align(Alignment.BottomEnd)
                .shadow(5.dp, CircleShape)
                .background(Color(0xFF06BEFB), CircleShape)
        ) {
            Icon(
                painter = painterResource(
                    id = if (filter == Filter.FAVORITES_ONLY) R.drawable.ic_star_favored
                    else R.drawable.ic_star_unfavored
                ),
                tint = Color.Black,
                contentDescription = stringResource(id = R.string.favorite_filter_content_description)
            )
        }
    }
}

@Composable
private fun AdsError(retryClicked: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.retry_text),
            color = Color.Black,
            fontSize = 18.sp
        )
        Button(onClick = retryClicked) {
            Text(text = stringResource(id = R.string.retry_button))
        }
    }
}

@Composable
private fun AdsLoading() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun AdList(
    ads: List<Ad>,
    favoriteClicked: (Ad) -> Unit
) {
    LazyColumn {
        itemsIndexed(
            items = ads,
            key = { _, item -> item.id }
        ) { index, ad ->
            AdItem(
                ad = ad,
                index = index,
                favoriteClicked = favoriteClicked
            )
        }
    }
}

@Composable
private fun AdItem(ad: Ad, index: Int, favoriteClicked: (Ad) -> Unit) {
    val hasImage = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(top = if (index != 0) 15.dp else 10.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.77f)
                    .shadow(5.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFDFDFDF))
            ) {
                if (ad.image != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(ad.image)
                            .crossfade(true)
                            .build(),
                        onSuccess = { hasImage.value = true },
                        contentDescription = ad.description,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.77f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.no_image),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                if (hasImage.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.4f)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0x00000000), Color(0xCC000000))
                                )
                            )
                            .align(Alignment.BottomCenter)
                    )
                }
                Text(
                    text = (ad.priceTotal ?: ad.priceValue ?: stringResource(id = R.string.no_price)).toString(),
                    fontSize = 15.sp,
                    color = Color(0xFFFFFFFF),
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 10.dp)
                        .align(Alignment.BottomStart)
                        .background(Color(0xB3000000), RoundedCornerShape(50.dp))
                        .padding(vertical = 2.dp, horizontal = 10.dp)
                )
                IconButton(
                    onClick = { favoriteClicked(ad) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (ad.isFavored) R.drawable.ic_star_favored else R.drawable.ic_star_unfavored
                        ),
                        contentDescription = stringResource(id = R.string.favorite_button_content_description),
                        tint = Color(0xFF06BEFB)
                    )
                }
            }
            Text(
                text = ad.description.orEmpty(),
                fontSize = 18.sp,
                color = Color(0xFF000000),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 10.dp)
            )
            Text(
                text = ad.location.orEmpty(),
                fontSize = 13.sp,
                color = Color(0xCC000000),
                modifier = Modifier
                    .padding(top = 4.dp)
            )
        }
    }
}
