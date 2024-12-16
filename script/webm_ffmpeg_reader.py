from moviepy.video.io.ffmpeg_reader import FFMPEG_VideoReader
from moviepy.config import get_setting
import subprocess as sp
import os
from moviepy.compat import DEVNULL
import re
from moviepy.tools import cvsecs


class WEBM_FFMPEG_VideoReader(FFMPEG_VideoReader):
    def __init__(self, filename, print_infos=False, bufsize=None,
                 pix_fmt="rgb24", check_duration=True,
                 target_resolution=None, resize_algo='bicubic',
                 fps_source='tbr'):
        self.filename = filename
        self.proc = None
        infos = ffmpeg_parse_infos(filename, print_infos, check_duration,
                                   fps_source)
        self.fps = infos['video_fps']
        self.size = infos['video_size']
        self.rotation = infos['video_rotation']

        if target_resolution:
            # revert the order, as ffmpeg used (width, height)
            target_resolution = target_resolution[1], target_resolution[0]

            if None in target_resolution:
                ratio = 1
                for idx, target in enumerate(target_resolution):
                    if target:
                        ratio = target / self.size[idx]
                self.size = (int(self.size[0] * ratio), int(self.size[1] * ratio))
            else:
                self.size = target_resolution
        self.resize_algo = resize_algo

        self.duration = infos['video_duration']
        self.ffmpeg_duration = infos['duration']
        self.nframes = infos['video_nframes']

        self.infos = infos

        self.pix_fmt = pix_fmt
        self.depth = 4 if pix_fmt == 'rgba' else 3

        if bufsize is None:
            w, h = self.size
            bufsize = self.depth * w * h + 100

        self.bufsize= bufsize
        self.initialize()


        self.pos = 1
        self.lastread = self.read_frame()

    def initialize(self, starttime=0):
        self.close()  # if any

        if starttime != 0:
            offset = min(1, starttime)
            i_arg = ['-ss', "%.06f" % (starttime - offset),
                     '-i', self.filename,
                     '-ss', "%.06f" % offset]
        else:
            is_webm = self.filename.endswith('.webm')
            if is_webm:
                i_arg = ["-c:v", "libvpx-vp9", '-i', self.filename]
            else:
                i_arg = ['-i', self.filename]

        cmd = ([get_setting("FFMPEG_BINARY")] + i_arg +
               ['-loglevel', 'error',
                '-f', 'image2pipe',
                '-vf', 'scale=%d:%d' % tuple(self.size),
                '-sws_flags', self.resize_algo,
                "-pix_fmt", self.pix_fmt,
                '-vcodec', 'rawvideo', '-'])

        popen_params = {"bufsize": self.bufsize,
                        "stdout": sp.PIPE,
                        "stderr": sp.PIPE,
                        "stdin": DEVNULL}

        if os.name == "nt":
            popen_params["creationflags"] = 0x08000000

        self.proc = sp.Popen(cmd, **popen_params)


def ffmpeg_parse_infos(filename, print_infos=False, check_duration=True,
                       fps_source='tbr'):
    # open the file in a pipe, provoke an error, read output
    is_GIF = filename.endswith('.gif')
    is_webm = filename.endswith('.webm')
    if is_webm:
        cmd = [get_setting("FFMPEG_BINARY"), "-c:v", "libvpx-vp9", "-i", filename]
    else:
        cmd = [get_setting("FFMPEG_BINARY"), "-i", filename]
    # 读取webm文件
    # cmd = [get_setting("FFMPEG_BINARY"), "-c:v", "libvpx-vp9", "-i", filename]
    if is_GIF:
        cmd += ["-f", "null", "/dev/null"]

    popen_params = {"bufsize": 10 ** 5,
                    "stdout": sp.PIPE,
                    "stderr": sp.PIPE,
                    "stdin": DEVNULL}

    if os.name == "nt":
        popen_params["creationflags"] = 0x08000000

    proc = sp.Popen(cmd, **popen_params)
    (output, error) = proc.communicate()
    infos = error.decode('utf8')

    del proc

    if print_infos:
        # print the whole info text returned by FFMPEG
        print(infos)

    lines = infos.splitlines()
    if "No such file or directory" in lines[-1]:
        raise IOError(("MoviePy error: the file %s could not be found!\n"
                       "Please check that you entered the correct "
                       "path.") % filename)

    result = dict()

    # get duration (in seconds)
    result['duration'] = None

    if check_duration:
        try:
            keyword = ('frame=' if is_GIF else 'Duration: ')
            # for large GIFS the "full" duration is presented as the last element in the list.
            index = -1 if is_GIF else 0
            line = [l for l in lines if keyword in l][index]
            match = re.findall("([0-9][0-9]:[0-9][0-9]:[0-9][0-9].[0-9][0-9])", line)[0]
            result['duration'] = cvsecs(match)
        except:
            raise IOError(("MoviePy error: failed to read the duration of file %s.\n"
                           "Here are the file infos returned by ffmpeg:\n\n%s") % (
                              filename, infos))

    # get the output line that speaks about video
    lines_video = [l for l in lines if ' Video: ' in l and re.search('\d+x\d+', l)]

    result['video_found'] = (lines_video != [])

    if result['video_found']:
        try:
            line = lines_video[0]

            # get the size, of the form 460x320 (w x h)
            match = re.search(" [0-9]*x[0-9]*(,| )", line)
            s = list(map(int, line[match.start():match.end() - 1].split('x')))
            result['video_size'] = s
        except:
            raise IOError(("MoviePy error: failed to read video dimensions in file %s.\n"
                           "Here are the file infos returned by ffmpeg:\n\n%s") % (
                              filename, infos))

        def get_tbr():
            match = re.search("( [0-9]*.| )[0-9]* tbr", line)

            # Sometimes comes as e.g. 12k. We need to replace that with 12000.
            s_tbr = line[match.start():match.end()].split(' ')[1]
            if "k" in s_tbr:
                tbr = float(s_tbr.replace("k", "")) * 1000
            else:
                tbr = float(s_tbr)
            return tbr

        def get_fps():
            match = re.search("( [0-9]*.| )[0-9]* fps", line)
            fps = float(line[match.start():match.end()].split(' ')[1])
            return fps

        if fps_source == 'tbr':
            try:
                result['video_fps'] = get_tbr()
            except:
                result['video_fps'] = get_fps()

        elif fps_source == 'fps':
            try:
                result['video_fps'] = get_fps()
            except:
                result['video_fps'] = get_tbr()

        # It is known that a fps of 24 is often written as 24000/1001
        # but then ffmpeg nicely rounds it to 23.98, which we hate.
        coef = 1000.0/1001.0
        fps = result['video_fps']
        for x in [23,24,25,30,50]:
            if (fps!=x) and abs(fps - x*coef) < .01:
                result['video_fps'] = x*coef

        if check_duration:
            result['video_nframes'] = int(result['duration']*result['video_fps'])+1
            result['video_duration'] = result['duration']
        else:
            result['video_nframes'] = 1
            result['video_duration'] = None
        # We could have also recomputed the duration from the number
        # of frames, as follows:
        # >>> result['video_duration'] = result['video_nframes'] / result['video_fps']

        # get the video rotation info.
        try:
            rotation_lines = [l for l in lines if 'rotate          :' in l and re.search('\d+$', l)]
            if len(rotation_lines):
                rotation_line = rotation_lines[0]
                match = re.search('\d+$', rotation_line)
                result['video_rotation'] = int(rotation_line[match.start() : match.end()])
            else:
                result['video_rotation'] = 0
        except:
            raise IOError(("MoviePy error: failed to read video rotation in file %s.\n"
                           "Here are the file infos returned by ffmpeg:\n\n%s")%(
                              filename, infos))


    lines_audio = [l for l in lines if ' Audio: ' in l]

    result['audio_found'] = lines_audio != []

    if result['audio_found']:
        line = lines_audio[0]
        try:
            match = re.search(" [0-9]* Hz", line)
            hz_string = line[match.start()+1:match.end()-3]  # Removes the 'hz' from the end
            result['audio_fps'] = int(hz_string)
        except:
            result['audio_fps'] = 'unknown'

    return result