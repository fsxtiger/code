from moviepy.video.io.ffmpeg_writer import FFMPEG_VideoWriter
import proglog

import numpy as np

class WEBM_FFMPEG_VideoWriter(FFMPEG_VideoWriter):
    def __init__(self, filename, size, fps, codec="libx264", audiofile=None,
                 preset="medium", bitrate=None, withmask=False,
                 logfile=None, threads=None, ffmpeg_params=None):

        super().__init__(filename, size, fps, codec=codec, audiofile=audiofile,
                       preset=preset, bitrate=bitrate, withmask=withmask,
                       logfile=logfile, threads=threads, ffmpeg_params=ffmpeg_params)


def ffmpeg_write_video(clip, filename, fps, codec="libx264", bitrate=None,
                       preset="medium", withmask=False, write_logfile=False,
                       audiofile=None, verbose=True, threads=None, ffmpeg_params=None,
                       logger='bar'):
    logger = proglog.default_bar_logger(logger)

    if write_logfile:
        logfile = open(filename + ".log", 'w+')
    else:
        logfile = None
    logger(message='Moviepy - Writing video %s\n' % filename)
    is_webm = filename.endswith('.webm')
    with WEBM_FFMPEG_VideoWriter(filename, clip.size, fps, codec = codec,
                                 preset=preset, bitrate=bitrate, withmask=is_webm, logfile=logfile,
                                 audiofile=audiofile, threads=threads,
                                 ffmpeg_params=ffmpeg_params) as writer:

        nframes = int(clip.duration*fps)

        for t,frame in clip.iter_frames(logger=logger, with_times=True,
                                        fps=fps, dtype="uint8"):
            if withmask:
                mask = (255*clip.mask.get_frame(t))
                if mask.dtype != "uint8":
                    mask = mask.astype("uint8")
                frame = np.dstack([frame,mask])

            writer.write_frame(frame)

    if write_logfile:
        logfile.close()
    logger(message='Moviepy - Done !')
