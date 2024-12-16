from moviepy.editor import CompositeVideoClip

import os
import proglog

from moviepy.decorators import (convert_masks_to_RGB, requires_duration, use_clip_fps_by_default)
from moviepy.Clip import Clip

from moviepy.tools import (extensions_dict, find_extension, is_string)
from webm_ffmpeg_writer import ffmpeg_write_video



class WebmCompositeVideoClip(CompositeVideoClip):
    def __init__(self, clips, size=None, bg_color=None, use_bgclip=False,
                 ismask=False):
        super().__init__(clips, size=size, bg_color=bg_color, use_bgclip=use_bgclip,
                         ismask=ismask)

    @requires_duration
    @use_clip_fps_by_default
    @convert_masks_to_RGB
    def write_videofile(self, filename, fps=None, codec=None,
                        bitrate=None, audio=True, audio_fps=44100,
                        preset="medium",
                        audio_nbytes=4, audio_codec=None,
                        audio_bitrate=None, audio_bufsize=2000,
                        temp_audiofile=None,
                        rewrite_audio=True, remove_temp=True,
                        write_logfile=False, verbose=True,
                        threads=None, ffmpeg_params=None,
                        logger='bar'):

        name, ext = os.path.splitext(os.path.basename(filename))
        ext = ext[1:].lower()
        logger = proglog.default_bar_logger(logger)

        if codec is None:

            try:
                codec = extensions_dict[ext]['codec'][0]
            except KeyError:
                raise ValueError("MoviePy couldn't find the codec associated "
                                 "with the filename. Provide the 'codec' "
                                 "parameter in write_videofile.")

        if audio_codec is None:
            if ext in ['ogv', 'webm']:
                audio_codec = 'libvorbis'
            else:
                audio_codec = 'libmp3lame'
        elif audio_codec == 'raw16':
            audio_codec = 'pcm_s16le'
        elif audio_codec == 'raw32':
            audio_codec = 'pcm_s32le'

        audiofile = audio if is_string(audio) else None
        make_audio = ((audiofile is None) and (audio == True) and
                      (self.audio is not None))

        if make_audio and temp_audiofile:
            # The audio will be the clip's audio
            audiofile = temp_audiofile
        elif make_audio:
            audio_ext = find_extension(audio_codec)
            audiofile = (name + Clip._TEMP_FILES_PREFIX + "wvf_snd.%s" % audio_ext)

        # enough cpu for multiprocessing ? USELESS RIGHT NOW, WILL COME AGAIN
        # enough_cpu = (multiprocessing.cpu_count() > 1)
        logger(message="Moviepy - Building video %s." % filename)
        if make_audio:
            self.audio.write_audiofile(audiofile, audio_fps,
                                       audio_nbytes, audio_bufsize,
                                       audio_codec, bitrate=audio_bitrate,
                                       write_logfile=write_logfile,
                                       verbose=verbose,
                                       logger=logger)
        is_webm = filename.endswith('.webm')
        ffmpeg_write_video(self, filename, fps, codec,
                           bitrate=bitrate,
                           preset=preset,
                           withmask=is_webm,
                           write_logfile=write_logfile,
                           audiofile=audiofile,
                           verbose=verbose, threads=threads,
                           ffmpeg_params=ffmpeg_params,
                           logger=logger)

        if remove_temp and make_audio:
            if os.path.exists(audiofile):
                os.remove(audiofile)
        logger(message="Moviepy - video ready %s" % filename)