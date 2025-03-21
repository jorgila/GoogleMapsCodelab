package com.example.mountainmarkers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mountainmarkers.data.utils.DMS
import com.example.mountainmarkers.data.utils.Direction
import com.example.mountainmarkers.data.utils.toDecimalDegrees
import com.example.mountainmarkers.presentation.AdvancedMarkersMapContent
import com.example.mountainmarkers.presentation.BasicMarkersMapContent
import com.example.mountainmarkers.presentation.ClusteringMarkersMapContent
import com.example.mountainmarkers.presentation.MountainsScreenEvent
import com.example.mountainmarkers.presentation.MountainsScreenViewState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Shows a [GoogleMap] with collection of markers
 */
@Composable
fun MountainMap(
    paddingValues: PaddingValues,
    viewState: MountainsScreenViewState.MountainList,
    eventFlow: Flow<MountainsScreenEvent>,
    selectedMarkerType: MarkerType,
) {
    var isMapLoaded by remember { mutableStateOf(false) }

    // TODO: Create cameraPositionState
    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(viewState.boundingBox.center, 5f)
    }
    // TODO: Create scope from rememberCoroutineScope
    val scope = rememberCoroutineScope()

    //   Add LaunchedEffect to zoom when the bounding box changes

    LaunchedEffect(key1 = viewState.boundingBox) {
        zoomAll(scope, cameraPositionState, viewState.boundingBox)
    }

    //   Add LaunchedEffect to react to events from the ViewModel

    LaunchedEffect(true) {
        eventFlow.collect { event ->
            when(event){
                MountainsScreenEvent.OnZoomAll -> {
                    zoomAll(scope, cameraPositionState, viewState.boundingBox)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // TODO: Add GoogleMap here

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            onMapLoaded = { isMapLoaded = true },
            googleMapOptionsFactory = {
                GoogleMapOptions().mapId("5498c55c1fa4107")
            },
        // cameraPositionState to GoogleMap
            cameraPositionState = cameraPositionState
        ){
        // GoogleMap content
            when (selectedMarkerType) {
                MarkerType.Basic -> {
                    BasicMarkersMapContent(
                        mountains = viewState.mountains,
                    )
                }

                MarkerType.Advanced -> {
                    AdvancedMarkersMapContent(
                        mountains = viewState.mountains,
                    )
                }

                MarkerType.Clustered -> {
                    ClusteringMarkersMapContent(
                        mountains = viewState.mountains,
                    )
                }
            }

            // Call to ColoradoPolygon
            ColoradoPolygon()

            // TODO: Add code to add KmlLayer.  Inside the GoogleMap content, but outside of the when
            // statement

        }





        // TODO: Add ScaleBar outside of of the GoogleMap content

        if (!isMapLoaded) {
            AnimatedVisibility(
                modifier = Modifier.matchParentSize(),
                visible = !isMapLoaded,
                enter = EnterTransition.None,
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .wrapContentSize()
                )
            }
        }
    }
}

// TODO: Create zoomAll function

fun zoomAll(
    scope: CoroutineScope,
    cameraPositionState: CameraPositionState,
    boundingBox: LatLngBounds
) {
    scope.launch {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngBounds(boundingBox, 64),
            durationMs = 1000
        )
    }
}

// TODO: Create ColoradoPolygon function

@Composable
@GoogleMapComposable
fun ColoradoPolygon(){
    val north = 41.0
    val south = 37.0
    val east = DMS(Direction.WEST,102.0,3.0).toDecimalDegrees()
    val west = DMS(Direction.WEST,109.0, 3.0).toDecimalDegrees()

    val locations = listOf(
        LatLng(north,east),
        LatLng(south,east),
        LatLng(south,west),
        LatLng(north,west)
    )

    Polygon(
        points = locations,
        strokeColor = MaterialTheme.colorScheme.tertiary,
        strokeWidth = 3F,
        fillColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
    )

}