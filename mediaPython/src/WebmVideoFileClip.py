from moviepy.editor import VideoFileClip
from webm_ffmpeg_reader import WEBM_FFMPEG_VideoReader


class WebmVideoFileClip(VideoFileClip):
    def __init__(self, filename, has_mask=False, audio=True, audio_buffersize=200000, target_resolution=None,
                 resize_algorithm='bicubic', audio_fps=44100, audio_nbytes=2, verbose=False, fps_source='tbr'):

        super().__init__(filename, has_mask=has_mask,
                         audio=audio, audio_buffersize=audio_buffersize,
                         target_resolution=target_resolution, resize_algorithm=resize_algorithm,
                         audio_fps=audio_fps, audio_nbytes=audio_nbytes, verbose=verbose,
                         fps_source='tbr')
        pix_fmt = "rgba" if has_mask else "rgb24"

        self.reader = WEBM_FFMPEG_VideoReader(filename, pix_fmt=pix_fmt,
                                              target_resolution=target_resolution,
                                              resize_algo=resize_algorithm,
                                              fps_source=fps_source)



