/*
 * Copyright (C) 2012 CyberAgent
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.easydarwin.video.render.core;

import java.util.LinkedList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage3x3ConvolutionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImage3x3TextureSamplingFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageAddBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageAlphaBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBoxBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBulgeDistortionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageCGAColorspaceFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageChromaKeyBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorBalanceFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorBurnBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorDodgeBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorInvertFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageCrosshatchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDarkenBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDifferenceBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDilationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDirectionalSobelEdgeDetectionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDissolveBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDivideBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageExclusionBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFalseColorFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGlassSphereFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHardLightBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHazeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageKuwaharaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLaplacianFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLightenBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLinearBurnBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLookupFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLuminosityBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMultiplyBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageNonMaximumSuppressionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageNormalBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageOpacityFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageOverlayBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePixelationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageRGBDilationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageRGBFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageScreenBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSmoothToonFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSobelEdgeDetection;
import jp.co.cyberagent.android.gpuimage.GPUImageSoftLightBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSourceOverBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSphereRefractionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSubtractBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSwirlFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageToneCurveFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageToonFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageTwoInputFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageWeakPixelInclusionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageWhiteBalanceFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageZoomBlurFilter;
import android.graphics.PointF;

public class ImageFilterTools {

	public static GPUImageFilter createFilterForType(final FilterType type) {
		switch (type) {
			case CONTRAST:
				return new GPUImageContrastFilter(2.0f);
			case GAMMA:
				return new GPUImageGammaFilter(2.0f);
			case INVERT:
				return new GPUImageColorInvertFilter();
			case PIXELATION:
				return new GPUImagePixelationFilter();
			case HUE:
				return new GPUImageHueFilter(90.0f);
			case BRIGHTNESS:
				return new GPUImageBrightnessFilter(1.5f);
			case GRAYSCALE:
				return new GPUImageGrayscaleFilter();
			case SEPIA:
				return new GPUImageSepiaFilter();
			case SHARPEN:
				GPUImageSharpenFilter sharpness = new GPUImageSharpenFilter();
				sharpness.setSharpness(2.0f);
				return sharpness;
			case SOBEL_EDGE_DETECTION:
				return new GPUImageSobelEdgeDetection();
			case THREE_X_THREE_CONVOLUTION:
				GPUImage3x3ConvolutionFilter convolution = new GPUImage3x3ConvolutionFilter();
				convolution.setConvolutionKernel(new float[] { -1.0f, 0.0f, 1.0f, -2.0f, 0.0f, 2.0f, -1.0f, 0.0f, 1.0f });
				return convolution;
			case EMBOSS:
				return new GPUImageEmbossFilter();
			case POSTERIZE:
				return new GPUImagePosterizeFilter();
			case FILTER_GROUP:
				List<GPUImageFilter> filters = new LinkedList<GPUImageFilter>();
				filters.add(new GPUImageContrastFilter());
				filters.add(new GPUImageDirectionalSobelEdgeDetectionFilter());
				filters.add(new GPUImageGrayscaleFilter());
				return new GPUImageFilterGroup(filters);
			case SATURATION:
				return new GPUImageSaturationFilter(1.0f);
			case EXPOSURE:
				return new GPUImageExposureFilter(0.0f);
			case HIGHLIGHT_SHADOW:
				return new GPUImageHighlightShadowFilter(0.0f, 1.0f);
			case MONOCHROME:
				return new GPUImageMonochromeFilter(1.0f, new float[] { 0.6f, 0.45f, 0.3f, 1.0f });
			case OPACITY:
				return new GPUImageOpacityFilter(1.0f);
			case RGB:
				return new GPUImageRGBFilter(1.0f, 1.0f, 1.0f);
			case WHITE_BALANCE:
				return new GPUImageWhiteBalanceFilter(5000.0f, 0.0f);
			case VIGNETTE:
				PointF centerPoint = new PointF();
				centerPoint.x = 0.5f;
				centerPoint.y = 0.5f;
				return new GPUImageVignetteFilter(centerPoint, new float[] { 0.0f, 0.0f, 0.0f }, 0.3f, 0.75f);
			case TONE_CURVE:
				GPUImageToneCurveFilter toneCurveFilter = new GPUImageToneCurveFilter();
				return toneCurveFilter;
			case BLEND_DIFFERENCE:
				return createBlendFilter(GPUImageDifferenceBlendFilter.class);
			case BLEND_SOURCE_OVER:
				return createBlendFilter(GPUImageSourceOverBlendFilter.class);
			case BLEND_COLOR_BURN:
				return createBlendFilter(GPUImageColorBurnBlendFilter.class);
			case BLEND_COLOR_DODGE:
				return createBlendFilter(GPUImageColorDodgeBlendFilter.class);
			case BLEND_DARKEN:
				return createBlendFilter(GPUImageDarkenBlendFilter.class);
			case BLEND_DISSOLVE:
				return createBlendFilter(GPUImageDissolveBlendFilter.class);
			case BLEND_EXCLUSION:
				return createBlendFilter(GPUImageExclusionBlendFilter.class);
			case BLEND_HARD_LIGHT:
				return createBlendFilter(GPUImageHardLightBlendFilter.class);
			case BLEND_LIGHTEN:
				return createBlendFilter(GPUImageLightenBlendFilter.class);
			case BLEND_ADD:
				return createBlendFilter(GPUImageAddBlendFilter.class);
			case BLEND_DIVIDE:
				return createBlendFilter(GPUImageDivideBlendFilter.class);
			case BLEND_MULTIPLY:
				return createBlendFilter(GPUImageMultiplyBlendFilter.class);
			case BLEND_OVERLAY:
				return createBlendFilter(GPUImageOverlayBlendFilter.class);
			case BLEND_SCREEN:
				return createBlendFilter(GPUImageScreenBlendFilter.class);
			case BLEND_ALPHA:
				return createBlendFilter(GPUImageAlphaBlendFilter.class);
			case BLEND_COLOR:
				return createBlendFilter(GPUImageColorBlendFilter.class);
			case BLEND_HUE:
				return createBlendFilter(GPUImageHueBlendFilter.class);
			case BLEND_SATURATION:
				return createBlendFilter(GPUImageSaturationBlendFilter.class);
			case BLEND_LUMINOSITY:
				return createBlendFilter(GPUImageLuminosityBlendFilter.class);
			case BLEND_LINEAR_BURN:
				return createBlendFilter(GPUImageLinearBurnBlendFilter.class);
			case BLEND_SOFT_LIGHT:
				return createBlendFilter(GPUImageSoftLightBlendFilter.class);
			case BLEND_SUBTRACT:
				return createBlendFilter(GPUImageSubtractBlendFilter.class);
			case BLEND_CHROMA_KEY:
				return createBlendFilter(GPUImageChromaKeyBlendFilter.class);
			case BLEND_NORMAL:
				return createBlendFilter(GPUImageNormalBlendFilter.class);
			case LOOKUP_AMATORKA:
				return new GPUImageLookupFilter();
			case GAUSSIAN_BLUR:
				return new GPUImageGaussianBlurFilter();
			case CROSSHATCH:
				return new GPUImageCrosshatchFilter();
			case BOX_BLUR:
				return new GPUImageBoxBlurFilter();
			case CGA_COLORSPACE:
				return new GPUImageCGAColorspaceFilter();
			case DILATION:
				return new GPUImageDilationFilter();
			case KUWAHARA:
				return new GPUImageKuwaharaFilter();
			case RGB_DILATION:
				return new GPUImageRGBDilationFilter();
			case SKETCH:
				return new GPUImageSketchFilter();
			case TOON:
				return new GPUImageToonFilter();
			case SMOOTH_TOON:
				return new GPUImageSmoothToonFilter();
			case BULGE_DISTORTION:
				return new GPUImageBulgeDistortionFilter();
			case GLASS_SPHERE:
				return new GPUImageGlassSphereFilter();
			case HAZE:
				return new GPUImageHazeFilter();
			case LAPLACIAN:
				return new GPUImageLaplacianFilter();
			case NON_MAXIMUM_SUPPRESSION:
				return new GPUImageNonMaximumSuppressionFilter();
			case SPHERE_REFRACTION:
				return new GPUImageSphereRefractionFilter();
			case SWIRL:
				return new GPUImageSwirlFilter();
			case WEAK_PIXEL_INCLUSION:
				return new GPUImageWeakPixelInclusionFilter();
			case FALSE_COLOR:
				return new GPUImageFalseColorFilter();
			case COLOR_BALANCE:
				return new GPUImageColorBalanceFilter();
			case ZOOM_BLUR:
				return new GPUImageZoomBlurFilter();
			default:
				return null;
		}

	}

	private static GPUImageFilter createBlendFilter(Class<? extends GPUImageTwoInputFilter> filterClass) {
		try {
			GPUImageTwoInputFilter filter = filterClass.newInstance();
			return filter;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public interface OnGpuImageFilterChosenListener {
		void onGpuImageFilterChosenListener(GPUImageFilter filter);
	}

	public enum FilterType {
		CONTRAST,
		GRAYSCALE,
		SHARPEN,
		SEPIA,
		SOBEL_EDGE_DETECTION,
		THREE_X_THREE_CONVOLUTION,
		FILTER_GROUP,
		EMBOSS,
		POSTERIZE,
		GAMMA,
		BRIGHTNESS,
		INVERT,
		HUE,
		PIXELATION,
		SATURATION,
		EXPOSURE,
		HIGHLIGHT_SHADOW,
		MONOCHROME,
		OPACITY,
		RGB,
		WHITE_BALANCE,
		VIGNETTE,
		TONE_CURVE,
		BLEND_COLOR_BURN,
		BLEND_COLOR_DODGE,
		BLEND_DARKEN,
		BLEND_DIFFERENCE,
		BLEND_DISSOLVE,
		BLEND_EXCLUSION,
		BLEND_SOURCE_OVER,
		BLEND_HARD_LIGHT,
		BLEND_LIGHTEN,
		BLEND_ADD,
		BLEND_DIVIDE,
		BLEND_MULTIPLY,
		BLEND_OVERLAY,
		BLEND_SCREEN,
		BLEND_ALPHA,
		BLEND_COLOR,
		BLEND_HUE,
		BLEND_SATURATION,
		BLEND_LUMINOSITY,
		BLEND_LINEAR_BURN,
		BLEND_SOFT_LIGHT,
		BLEND_SUBTRACT,
		BLEND_CHROMA_KEY,
		BLEND_NORMAL,
		LOOKUP_AMATORKA,
		GAUSSIAN_BLUR,
		CROSSHATCH,
		BOX_BLUR,
		CGA_COLORSPACE,
		DILATION,
		KUWAHARA,
		RGB_DILATION,
		SKETCH,
		TOON,
		SMOOTH_TOON,
		BULGE_DISTORTION,
		GLASS_SPHERE,
		HAZE,
		LAPLACIAN,
		NON_MAXIMUM_SUPPRESSION,
		SPHERE_REFRACTION,
		SWIRL,
		WEAK_PIXEL_INCLUSION,
		FALSE_COLOR,
		COLOR_BALANCE,
		ZOOM_BLUR
	}

	public static class FilterAdjuster {
		private final Adjuster<? extends GPUImageFilter> adjuster;

		public FilterAdjuster(final GPUImageFilter filter) {
			if (filter instanceof GPUImageAlphaBlendFilter) {
				adjuster = new AlphaBlendAdjuster().filter(filter);
			} else if (filter instanceof GPUImageSharpenFilter) {
				adjuster = new SharpnessAdjuster().filter(filter);
			} else if (filter instanceof GPUImageSepiaFilter) {
				adjuster = new SepiaAdjuster().filter(filter);
			} else if (filter instanceof GPUImageContrastFilter) {
				adjuster = new ContrastAdjuster().filter(filter);
			} else if (filter instanceof GPUImageGammaFilter) {
				adjuster = new GammaAdjuster().filter(filter);
			} else if (filter instanceof GPUImageBrightnessFilter) {
				adjuster = new BrightnessAdjuster().filter(filter);
			} else if (filter instanceof GPUImageSobelEdgeDetection) {
				adjuster = new SobelAdjuster().filter(filter);
			} else if (filter instanceof GPUImageEmbossFilter) {
				adjuster = new EmbossAdjuster().filter(filter);
			} else if (filter instanceof GPUImage3x3TextureSamplingFilter) {
				adjuster = new GPU3x3TextureAdjuster().filter(filter);
			} else if (filter instanceof GPUImageHueFilter) {
				adjuster = new HueAdjuster().filter(filter);
			} else if (filter instanceof GPUImagePosterizeFilter) {
				adjuster = new PosterizeAdjuster().filter(filter);
			} else if (filter instanceof GPUImagePixelationFilter) {
				adjuster = new PixelationAdjuster().filter(filter);
			} else if (filter instanceof GPUImageSaturationFilter) {
				adjuster = new SaturationAdjuster().filter(filter);
			} else if (filter instanceof GPUImageExposureFilter) {
				adjuster = new ExposureAdjuster().filter(filter);
			} else if (filter instanceof GPUImageHighlightShadowFilter) {
				adjuster = new HighlightShadowAdjuster().filter(filter);
			} else if (filter instanceof GPUImageMonochromeFilter) {
				adjuster = new MonochromeAdjuster().filter(filter);
			} else if (filter instanceof GPUImageOpacityFilter) {
				adjuster = new OpacityAdjuster().filter(filter);
			} else if (filter instanceof GPUImageRGBFilter) {
				adjuster = new RGBAdjuster().filter(filter);
			} else if (filter instanceof GPUImageWhiteBalanceFilter) {
				adjuster = new WhiteBalanceAdjuster().filter(filter);
			} else if (filter instanceof GPUImageVignetteFilter) {
				adjuster = new VignetteAdjuster().filter(filter);
			} else if (filter instanceof GPUImageDissolveBlendFilter) {
				adjuster = new DissolveBlendAdjuster().filter(filter);
			} else if (filter instanceof GPUImageGaussianBlurFilter) {
				adjuster = new GaussianBlurAdjuster().filter(filter);
			} else if (filter instanceof GPUImageCrosshatchFilter) {
				adjuster = new CrosshatchBlurAdjuster().filter(filter);
			} else if (filter instanceof GPUImageBulgeDistortionFilter) {
				adjuster = new BulgeDistortionAdjuster().filter(filter);
			} else if (filter instanceof GPUImageGlassSphereFilter) {
				adjuster = new GlassSphereAdjuster().filter(filter);
			} else if (filter instanceof GPUImageHazeFilter) {
				adjuster = new HazeAdjuster().filter(filter);
			} else if (filter instanceof GPUImageSphereRefractionFilter) {
				adjuster = new SphereRefractionAdjuster().filter(filter);
			} else if (filter instanceof GPUImageSwirlFilter) {
				adjuster = new SwirlAdjuster().filter(filter);
			} else if (filter instanceof GPUImageColorBalanceFilter) {
				adjuster = new ColorBalanceAdjuster().filter(filter);
			} else if (filter instanceof GPUImageZoomBlurFilter) {
				adjuster = new ZoomBlurAdjuster().filter(filter);
			} else {
				adjuster = null;
			}
		}

		public boolean canAdjust() {
			return adjuster != null;
		}

		public void adjust(final int percentage) {
			if (adjuster != null) {
				adjuster.adjust(percentage);
			}
		}

		private abstract class Adjuster<T extends GPUImageFilter> {
			private T filter;

			@SuppressWarnings("unchecked")
			public Adjuster<T> filter(final GPUImageFilter filter) {
				this.filter = (T) filter;
				return this;
			}

			public T getFilter() {
				return filter;
			}

			public abstract void adjust(int percentage);

			protected float range(final int percentage, final float start, final float end) {
				return (end - start) * percentage / 100.0f + start;
			}

			protected int range(final int percentage, final int start, final int end) {
				return (end - start) * percentage / 100 + start;
			}
		}

		private class SharpnessAdjuster extends Adjuster<GPUImageSharpenFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setSharpness(range(percentage, -4.0f, 4.0f));
			}
		}

		private class PixelationAdjuster extends Adjuster<GPUImagePixelationFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setPixel(range(percentage, 1.0f, 100.0f));
			}
		}

		private class HueAdjuster extends Adjuster<GPUImageHueFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setHue(range(percentage, 0.0f, 360.0f));
			}
		}

		private class ContrastAdjuster extends Adjuster<GPUImageContrastFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setContrast(range(percentage, 0.0f, 2.0f));
			}
		}

		private class GammaAdjuster extends Adjuster<GPUImageGammaFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setGamma(range(percentage, 0.0f, 3.0f));
			}
		}

		private class BrightnessAdjuster extends Adjuster<GPUImageBrightnessFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setBrightness(range(percentage, -1.0f, 1.0f));
			}
		}

		private class SepiaAdjuster extends Adjuster<GPUImageSepiaFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setIntensity(range(percentage, 0.0f, 2.0f));
			}
		}

		private class SobelAdjuster extends Adjuster<GPUImageSobelEdgeDetection> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setLineSize(range(percentage, 0.0f, 5.0f));
			}
		}

		private class EmbossAdjuster extends Adjuster<GPUImageEmbossFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setIntensity(range(percentage, 0.0f, 4.0f));
			}
		}

		private class PosterizeAdjuster extends Adjuster<GPUImagePosterizeFilter> {
			@Override
			public void adjust(final int percentage) {
				// In theorie to 256, but only first 50 are interesting
				getFilter().setColorLevels(range(percentage, 1, 50));
			}
		}

		private class GPU3x3TextureAdjuster extends Adjuster<GPUImage3x3TextureSamplingFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setLineSize(range(percentage, 0.0f, 5.0f));
			}
		}

		private class SaturationAdjuster extends Adjuster<GPUImageSaturationFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setSaturation(range(percentage, 0.0f, 2.0f));
			}
		}

		private class ExposureAdjuster extends Adjuster<GPUImageExposureFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setExposure(range(percentage, -10.0f, 10.0f));
			}
		}

		private class HighlightShadowAdjuster extends Adjuster<GPUImageHighlightShadowFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setShadows(range(percentage, 0.0f, 1.0f));
				getFilter().setHighlights(range(percentage, 0.0f, 1.0f));
			}
		}

		private class MonochromeAdjuster extends Adjuster<GPUImageMonochromeFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setIntensity(range(percentage, 0.0f, 1.0f));
				//getFilter().setColor(new float[]{0.6f, 0.45f, 0.3f, 1.0f});
			}
		}

		private class OpacityAdjuster extends Adjuster<GPUImageOpacityFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setOpacity(range(percentage, 0.0f, 1.0f));
			}
		}

		private class RGBAdjuster extends Adjuster<GPUImageRGBFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setRed(range(percentage, 0.0f, 1.0f));
				//getFilter().setGreen(range(percentage, 0.0f, 1.0f));
				//getFilter().setBlue(range(percentage, 0.0f, 1.0f));
			}
		}

		private class WhiteBalanceAdjuster extends Adjuster<GPUImageWhiteBalanceFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setTemperature(range(percentage, 2000.0f, 8000.0f));
				//getFilter().setTint(range(percentage, -100.0f, 100.0f));
			}
		}

		private class VignetteAdjuster extends Adjuster<GPUImageVignetteFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setVignetteStart(range(percentage, 0.0f, 1.0f));
			}
		}

		private class DissolveBlendAdjuster extends Adjuster<GPUImageDissolveBlendFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setMix(range(percentage, 0.0f, 1.0f));
			}
		}

		private class GaussianBlurAdjuster extends Adjuster<GPUImageGaussianBlurFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setBlurSize(range(percentage, 0.0f, 5.0f));
			}
		}

		private class CrosshatchBlurAdjuster extends Adjuster<GPUImageCrosshatchFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setCrossHatchSpacing(range(percentage, 0.0f, 0.06f));
				getFilter().setLineWidth(range(percentage, 0.0f, 0.006f));
			}
		}

		private class BulgeDistortionAdjuster extends Adjuster<GPUImageBulgeDistortionFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setRadius(range(percentage, 0.0f, 1.0f));
				getFilter().setScale(range(percentage, -1.0f, 1.0f));
			}
		}

		private class GlassSphereAdjuster extends Adjuster<GPUImageGlassSphereFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setRadius(range(percentage, 0.0f, 1.0f));
			}
		}

		private class HazeAdjuster extends Adjuster<GPUImageHazeFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setDistance(range(percentage, -0.3f, 0.3f));
				getFilter().setSlope(range(percentage, -0.3f, 0.3f));
			}
		}

		private class SphereRefractionAdjuster extends Adjuster<GPUImageSphereRefractionFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setRadius(range(percentage, 0.0f, 1.0f));
			}
		}

		private class SwirlAdjuster extends Adjuster<GPUImageSwirlFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setAngle(range(percentage, 0.0f, 2.0f));
			}
		}

		private class ColorBalanceAdjuster extends Adjuster<GPUImageColorBalanceFilter> {

			@Override
			public void adjust(int percentage) {
				getFilter()
					.setMidtones(new float[] { range(percentage, 0.0f, 1.0f), range(percentage / 2, 0.0f, 1.0f), range(percentage / 3, 0.0f, 1.0f) });
			}
		}

		private class AlphaBlendAdjuster extends Adjuster<GPUImageAlphaBlendFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setMix(range(percentage, 0.0f, 1.0f));
			}
		}

		private class ZoomBlurAdjuster extends Adjuster<GPUImageZoomBlurFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setBlurSize(range(percentage, 0.0f, 10.0f));
			}
		}
	}
}
