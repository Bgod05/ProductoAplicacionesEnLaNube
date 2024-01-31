package com.example.museoapp

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.museoapp.databinding.FragmentArBinding
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Config
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.utils.doOnApplyWindowInsets


class ArFragment : BaseFragment<FragmentArBinding>(FragmentArBinding::inflate) {

    private lateinit var sceneView: ArSceneView
    private lateinit var loadingView: View
    private lateinit var statusText: TextView
    private lateinit var placeModelButton: ExtendedFloatingActionButton
    private lateinit var newModelButton: ExtendedFloatingActionButton

    data class Model(
        val fileLocation: String,
        val scaleUnits: Float? = null,
        val placementMode: PlacementMode = PlacementMode.BEST_AVAILABLE,
        val applyPoseRotation: Boolean = true
    )

    private val models = listOf(
        Model(
            "models/botella_escultorica_nasca.glb",
            scaleUnits = 0.5f,
            placementMode = PlacementMode.BEST_AVAILABLE,
            applyPoseRotation = false
        ),
        Model(
            fileLocation = "https://firebasestorage.googleapis.com/v0/b/fresh-arcade-336514.appspot.com/o/cultures%2Fchavin%2Fcabeza_clava_rn_3347.glb?alt=media&token=0d5fa135-0c04-4df4-8123-be2651a8caf8",
            placementMode = PlacementMode.INSTANT,
            scaleUnits = 0.5f
        ),
        Model(
            fileLocation = "https://firebasestorage.googleapis.com/v0/b/mymuseo-8bab8.appspot.com/o/cultures%2Fmochica%2Ftorito.glb?alt=media&token=d43045a5-6089-4503-8c6c-3c7ac58997af",
            placementMode = PlacementMode.PLANE_HORIZONTAL,
            scaleUnits = 0.5f
        ),
        Model(
            fileLocation = "https://firebasestorage.googleapis.com/v0/b/mymuseo-8bab8.appspot.com/o/cultures%2Fmochica%2Fcantaro_chancay.glb?alt=media&token=dba9fcbc-cbbd-4a93-a0c2-220d69f020c1",
            scaleUnits = 1.5f,
            placementMode = PlacementMode.BEST_AVAILABLE,
            applyPoseRotation = false
        ),
    )

    private var modelIndex = 0
    private var modelNode: ArModelNode? = null

    private var isLoading = false
        set(value) {
            field = value
            loadingView.isGone = !value
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sceneView = binding.sceneView.apply {
            lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            depthEnabled = true
            instantPlacementEnabled = true
            onArTrackingFailureChanged = { reason ->
                statusText.text = reason?.getDescription(context)
                statusText.isGone = reason == null
            }
        }
        loadingView = binding.loadingView
        newModelButton = binding.newModelButton.apply {
            val bottomMargin = (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
            doOnApplyWindowInsets { systemBarsInsets ->
                (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                    systemBarsInsets.bottom + bottomMargin
            }
            setOnClickListener { newModelNode() }
        }
        placeModelButton = binding.placeModelButton.apply {
            setOnClickListener { placeModelNode() }
        }

        newModelNode()

    }

    private fun placeModelNode() {
        modelNode?.anchor()
        placeModelButton.isVisible = false
        sceneView.planeRenderer.isVisible = false
    }

    private fun newModelNode() {
        isLoading = true
        modelNode?.takeIf { !it.isAnchored }?.let {
            sceneView.removeChild(it)
            it.destroy()
        }
        val model = models[modelIndex]
        modelIndex = (modelIndex + 1) % models.size
        modelNode = ArModelNode(sceneView.engine, model.placementMode).apply {
            isSmoothPoseEnable = true
            applyPoseRotation = model.applyPoseRotation
            loadModelGlbAsync(
                glbFileLocation = model.fileLocation,
                autoAnimate = true,
                scaleToUnits = model.scaleUnits,
                centerOrigin = Position(y = -1.0f)
            ) {
                sceneView.planeRenderer.isVisible = true
                isLoading = false
            }
            onAnchorChanged = { anchor ->
                placeModelButton.isGone = anchor != null
            }
            onHitResult = { node, _ ->
                placeModelButton.isGone = !node.isTracking
            }
        }
        sceneView.addChild(modelNode!!)

        sceneView.selectedNode = modelNode
    }


}