/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.yaraki.animateit.deck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import io.github.yaraki.animateit.R
import io.github.yaraki.animateit.databinding.PageFadeThroughBinding
import io.github.yaraki.animateit.transition.FAST_OUT_LINEAR_IN
import io.github.yaraki.animateit.transition.FAST_OUT_SLOW_IN
import io.github.yaraki.animateit.transition.LINEAR_OUT_SLOW_IN
import kotlinx.coroutines.delay

class FadeThroughFragment : PageFragment() {

    companion object : Page {
        override fun create() = FadeThroughFragment()
    }

    private lateinit var binding: PageFadeThroughBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PageFadeThroughBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Glide.with(binding.content1Icon)
            .load(R.drawable.cheese_2)
            .transform(CircleCrop())
            .into(binding.content1Icon)

        binding.web.settings.run {
            javaScriptEnabled = true
            allowContentAccess = true
            setAppCacheEnabled(true)
        }
        binding.web.loadUrl("file:///android_asset/fade_through.html")

        val fadeThrough = TransitionSet().apply {
            addTransition(ChangeBounds().apply {
                duration = 2500
                interpolator = FAST_OUT_SLOW_IN
            })
            addTransition(TransitionSet().apply {
                ordering = TransitionSet.ORDERING_SEQUENTIAL
                addTransition(Fade(Fade.OUT).apply {
                    duration = 1250
                    interpolator = FAST_OUT_LINEAR_IN
                })
                addTransition(Fade(Fade.IN).apply {
                    duration = 1250
                    interpolator = LINEAR_OUT_SLOW_IN
                })
            })
        }

        lifecycleScope.launchWhenStarted {
            var count = 0
            while (true) {
                TransitionManager.beginDelayedTransition(binding.example, fadeThrough)
                if (count % 2 == 0) {
                    binding.content1.visibility = View.GONE
                    binding.content2.visibility = View.VISIBLE
                } else {
                    binding.content1.visibility = View.VISIBLE
                    binding.content2.visibility = View.GONE
                }
                ++count

                if (count > 1) {
                    delay(3000)
                } else {
                    delay(1000)
                }
            }
        }
    }
}
