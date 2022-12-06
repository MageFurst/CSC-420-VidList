import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaListPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;
import uk.co.caprica.vlcj.player.list.PlaybackMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MediaPlayer {

    JSlider vidprogress;

    List<String> original;

    public static void main(String[] args) throws IOException  {
        MediaPlayer m = new MediaPlayer();
    }

    public MediaPlayer() {

        String test = "C:\\Users\\Sal\\Desktop\\VidList\\build\\resources\\main\\test.mp4";
        String test2 = "C:\\Users\\Sal\\Desktop\\VidList\\build\\resources\\main\\test2.mp4";
        String test3 = "C:\\Users\\Sal\\Desktop\\VidList\\build\\resources\\main\\test3.mp4";
        AtomicBoolean paused = new AtomicBoolean(false);
        AtomicBoolean shuffled = new AtomicBoolean(false);
        AtomicBoolean looping = new AtomicBoolean(false);
        AtomicBoolean muted = new AtomicBoolean(false);
        AtomicBoolean playlisttoggle = new AtomicBoolean(false);
        AtomicBoolean ismini = new AtomicBoolean(false);


        EmbeddedMediaListPlayerComponent mediaPlayerComponent = new EmbeddedMediaListPlayerComponent();

        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new UpdateBar());

        mediaPlayerComponent.mediaListPlayer().list().media().add(test);
        mediaPlayerComponent.mediaListPlayer().list().media().add(test2);
        mediaPlayerComponent.mediaListPlayer().list().media().add(test3);

        JFrame frame = new JFrame();
        frame.setTitle("VidList Video Player");
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel functions = new JPanel();
        functions.setLayout(new BorderLayout());

        JPanel center = new JPanel();
        functions.add(center, BorderLayout.CENTER);

        JPanel left = new JPanel();
        functions.add(left, BorderLayout.WEST);

        JPanel right = new JPanel();
        functions.add(right, BorderLayout.EAST);

        JPanel volumepanel = new JPanel();
        JSlider volumeslider = new JSlider(JSlider.VERTICAL);
        volumeslider.setMaximum(100);
        volumeslider.setMinimum(0);
        volumepanel.setOpaque(false);
        volumeslider.setUI(new ProgressUI(volumeslider));
        volumeslider.setValue(50);
        volumeslider.addChangeListener(e ->
                mediaPlayerComponent.mediaListPlayer().mediaPlayer().mediaPlayer().audio().setVolume(volumeslider.getValue())
        );
        volumepanel.setVisible(false);

        JPanel playlistmanager = new JPanel(new BorderLayout());
        playlistmanager.setOpaque(false);

        JPanel playlistpanel = new JPanel(new GridLayout(25,0));
        playlistpanel.setOpaque(false);
        updatePlaylist(mediaPlayerComponent,playlistpanel);

        JPanel uploadpanel = new JPanel();
        uploadpanel.setOpaque(false);
        JButton uploadbutton = new JButton(new ImageIcon(getMedia("upload.png")));
        renderButton(uploadbutton,"upload.png","uploadhover.png");
        uploadbutton.setOpaque(false);
        uploadbutton.setContentAreaFilled(false);
        uploadbutton.setFocusPainted(false);

        uploadbutton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.showOpenDialog(null);
            String test4 = fc.getSelectedFile().toString();
            mediaPlayerComponent.mediaListPlayer().list().media().add(test4);
            playlistpanel.removeAll();
            updatePlaylist(mediaPlayerComponent,playlistpanel);
            playlistpanel.setVisible(false);
            playlistpanel.setVisible(true);
        });


        uploadpanel.add(uploadbutton);

        playlistmanager.add(playlistpanel, BorderLayout.NORTH);
        playlistmanager.add(uploadpanel,BorderLayout.SOUTH);
        playlistmanager.setVisible(false);

        JButton play = new JButton(new ImageIcon(getMedia("play.png")));
        renderButtonToggle(play,"play.png","playhover.png",paused);
        play.addActionListener(e -> {
            mediaPlayerComponent.mediaPlayer().controls().pause();
            if(paused.get()){
                paused.set(false);
                play.setIcon(new ImageIcon(getMedia("playhover.png")));
            }
            else {
                paused.set(true);
                play.setIcon(new ImageIcon(getMedia("pause.png")));
            }
        });

        vidprogress = new JSlider(0, 1000);
        vidprogress.setUI(new ProgressUI(vidprogress));
        vidprogress.setOpaque(false);
        functions.add(vidprogress,BorderLayout.NORTH);
        vidprogress.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mediaPlayerComponent.mediaPlayer().controls().pause();
                play.setIcon(new ImageIcon(getMedia("pause.png")));
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                float pos = vidprogress.getValue() / 1000f;
                mediaPlayerComponent.mediaPlayer().controls().setPosition(pos);
                mediaPlayerComponent.mediaPlayer().controls().play();
                play.setIcon(new ImageIcon(getMedia("play.png")));
            }
        });

        JButton fast = new JButton(new ImageIcon(getMedia("fast.png")));
        renderButton(fast,"fast.png","fasthover.png");
        fast.addActionListener(e -> {
            mediaPlayerComponent.mediaPlayer().controls().skipTime(10000);
        });

        JButton rew = new JButton(new ImageIcon(getMedia("rew.png")));
        renderButton(rew,"rew.png","rewhover.png");
        rew.addActionListener(e -> {
            mediaPlayerComponent.mediaPlayer().controls().skipTime(-10000);
        });

        JButton next = new JButton(new ImageIcon(getMedia("next.png")));
        renderButton(next,"next.png","nexthover.png");
        next.addActionListener(e -> {
            mediaPlayerComponent.mediaListPlayer().controls().playNext();
        });


        JButton prev = new JButton(new ImageIcon(getMedia("prev.png")));
        renderButton(prev,"prev.png","prevhover.png");
        prev.addActionListener(e -> {
            mediaPlayerComponent.mediaListPlayer().controls().playPrevious();
        });

        JButton list = new JButton(new ImageIcon(getMedia("playlist.png")));
        renderButtonToggle(list,"playlist.png","playlisthover.png",playlisttoggle);
        list.addActionListener(e -> {
            if(playlisttoggle.get()){
                playlisttoggle.set(false);
                playlistmanager.setVisible(false);
                list.setIcon(new ImageIcon(getMedia("playlist.png")));
            }
            else {
                playlisttoggle.set(true);
                playlistmanager.setVisible(true);
                list.setIcon(new ImageIcon(getMedia("playlisthover.png")));
            }
        });

        JButton shuffle = new JButton(new ImageIcon(getMedia("shuffle.png")));
        renderButtonToggle(shuffle,"shuffle.png","shufflehover.png",shuffled);
        shuffle.addActionListener( e -> {
            if(shuffled.get()){
                shuffled.set(false);
                shuffle.setIcon(new ImageIcon(getMedia("shuffle.png")));
                List<String> modified = mediaPlayerComponent.mediaListPlayer().list().media().mrls();
                for(int i = 0; i < mediaPlayerComponent.mediaListPlayer().list().media().mrls().size(); i++){
                    mediaPlayerComponent.mediaListPlayer().list().media().remove(i);
                }
                Collections.shuffle(modified);
                for (String m : modified){
                    mediaPlayerComponent.mediaListPlayer().list().media().add(m);
                }
            }
            else {
                shuffled.set(true);
                shuffle.setIcon(new ImageIcon(getMedia("shufflehover.png")));
                for(int i = 0; i < mediaPlayerComponent.mediaListPlayer().list().media().mrls().size(); i++){
                    mediaPlayerComponent.mediaListPlayer().list().media().remove(i);
                }
                for (String o : original){
                    mediaPlayerComponent.mediaListPlayer().list().media().add(o);
                }
            }
        });

        JButton loop = new JButton(new ImageIcon(getMedia("loop.png")));
        renderButtonToggle(loop,"loop.png","loophover.png",looping);
        loop.addActionListener( e -> {
            if(looping.get()){
                looping.set(false);
                mediaPlayerComponent.mediaListPlayer().controls().setMode(PlaybackMode.DEFAULT);
                loop.setIcon(new ImageIcon(getMedia("loop.png")));
            }
            else {
                looping.set(true);
                mediaPlayerComponent.mediaListPlayer().controls().setMode(PlaybackMode.REPEAT);
                loop.setIcon(new ImageIcon(getMedia("loophover.png")));
            }
        });

        JButton volume = new JButton(new ImageIcon(getMedia("volume.png")));
        renderButtonToggle(volume,"volume.png","volumehover.png",muted);
        volume.addActionListener( e -> {
            if(muted.get()){
                muted.set(false);
                volume.setIcon(new ImageIcon(getMedia("volume.png")));
                volumepanel.setVisible(false);
            }
            else {
                muted.set(true);
                volume.setIcon(new ImageIcon(getMedia("volumehover.png")));
                volumepanel.setVisible(true);
            }
        });

        JButton mini = new JButton(new ImageIcon(getMedia("mini.png")));
        renderButton(mini,"mini.png","minihover.png");
        mini.addActionListener(e -> {
            if(ismini.get()){
                frame.setSize(1600,1000);
                ismini.set(false);
                next.setVisible(true);
                prev.setVisible(true);
                volume.setVisible(true);
                shuffle.setVisible(true);
                loop.setVisible(true);
                list.setVisible(true);
            }
            else {
                frame.setSize(750,750);
                ismini.set(true);
                next.setVisible(false);
                prev.setVisible(false);
                volume.setVisible(false);
                shuffle.setVisible(false);
                loop.setVisible(false);
                list.setVisible(false);
            }
        });

        JButton full = new JButton(new ImageIcon(getMedia("full.png")));
        renderButton(full,"full.png","fullhover.png");
        mediaPlayerComponent.mediaPlayer().fullScreen().strategy(
                new AdaptiveFullScreenStrategy(frame));
        full.addActionListener(e -> {
            mediaPlayerComponent.mediaPlayer().fullScreen().toggle();
        });

        volumepanel.add(volumeslider,BorderLayout.NORTH);

        left.add(volume);
        left.add(shuffle);
        left.add(loop);

        center.add(prev);
        center.add(rew);
        center.add(play);
        center.add(fast);
        center.add(next);

        right.add(list);
        right.add(mini);
        right.add(full);

        frame.add(volumepanel,BorderLayout.WEST);
        volumepanel.setBorder(BorderFactory.createEmptyBorder(600,70,10,10));

        frame.add(playlistmanager,BorderLayout.EAST);

        center.setOpaque(false);
        left.setOpaque(false);
        right.setOpaque(false);
        functions.setOpaque(false);

        frame.setBounds(100, 50, 1600, 1000);
        mediaPlayerComponent.setBounds(0,0,frame.getWidth(),frame.getHeight());
        functions.setBounds(0,(frame.getHeight() - 100),frame.getWidth(),50);

        frame.add(mediaPlayerComponent,BorderLayout.CENTER);
        frame.add(functions,BorderLayout.SOUTH);

        original = mediaPlayerComponent.mediaListPlayer().list().media().mrls();

        frame.setVisible(true);

        mediaPlayerComponent.mediaPlayer().audio().setVolume(50);

        mediaPlayerComponent.mediaListPlayer().controls().playNext();

    }

    public URL getMedia(String file){
        return this.getClass().getClassLoader().getResource(file);
    }

    public void renderButton(JButton b, String base, String hover){
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setIcon(new ImageIcon(getMedia(hover)));
                super.mouseEntered(e);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                b.setIcon(new ImageIcon(getMedia(base)));
                super.mouseExited(e);
            }
        });
    }

    public void renderButtonToggle(JButton b, String base, String hover, AtomicBoolean bool){
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if(!bool.get()){
                    b.setIcon(new ImageIcon(getMedia(hover)));
                }
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(!bool.get()){
                    b.setIcon(new ImageIcon(getMedia(base)));
                }
                super.mouseExited(e);
            }
        });
    }

    public void updatePlaylist(EmbeddedMediaListPlayerComponent emc, JPanel p){
        for(String x : emc.mediaListPlayer().list().media().mrls()){
            JButton b = new JButton(x);
            b.setOpaque(false);
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.addActionListener(e -> {
                emc.mediaListPlayer().controls().play(emc.mediaListPlayer().list().media().mrls().indexOf(x));
            });
            p.add(b);
        }
        original = emc.mediaListPlayer().list().media().mrls();
    }

    private class UpdateBar extends MediaPlayerEventAdapter {
        @Override
        public void positionChanged(uk.co.caprica.vlcj.player.base.MediaPlayer mediaPlayer, float newPosition) {
            int value = Math.min(1000,Math.round(newPosition * 1000));
            System.out.println(mediaPlayer.status().position());
            vidprogress.setValue(value);
            super.positionChanged(mediaPlayer, newPosition);
        }
    }

}