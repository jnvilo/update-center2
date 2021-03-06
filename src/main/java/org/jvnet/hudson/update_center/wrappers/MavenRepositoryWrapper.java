package org.jvnet.hudson.update_center.wrappers;

import hudson.util.VersionNumber;
import org.jvnet.hudson.update_center.ArtifactCoordinates;
import org.jvnet.hudson.update_center.HPI;
import org.jvnet.hudson.update_center.HudsonWar;
import org.jvnet.hudson.update_center.MavenArtifact;
import org.jvnet.hudson.update_center.MavenRepository;
import org.jvnet.hudson.update_center.PluginHistory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.Manifest;

public class MavenRepositoryWrapper implements MavenRepository {

    protected MavenRepository base;

    /** Should be called by subclasses who are decorating an existing MavenRepository instance. */
    void setBaseRepository(MavenRepository base) {
        this.base = base;
    }

    @Override
    public HPI createHpiArtifact(ArtifactCoordinates a, PluginHistory p) {
        return base.createHpiArtifact(a, p);
    }

    @Override
    public TreeMap<VersionNumber, HudsonWar> getHudsonWar() throws IOException {
        return base.getHudsonWar();
    }

    @Override
    public void listWar(TreeMap<VersionNumber, HudsonWar> r, String groupId, VersionNumber cap) throws IOException {
        base.listWar(r, groupId, cap);
    }

    @Override
    public Collection<ArtifactCoordinates> listAllPlugins() throws IOException {
        return base.listAllPlugins();
    }

    @Override
    public Digests getDigests(MavenArtifact artifact) throws IOException {
        return base.getDigests(artifact);
    }

    @Override
    public Manifest getManifest(MavenArtifact artifact) throws IOException {
        return base.getManifest(artifact);
    }

    @Override
    public InputStream getZipFileEntry(MavenArtifact artifact, String path) throws IOException {
        return base.getZipFileEntry(artifact, path);
    }

    @Override
    public File resolve(ArtifactCoordinates artifact) throws IOException {
        return base.resolve(artifact);
    }

    @Override
    public Collection<PluginHistory> listHudsonPlugins() throws IOException {
        return base.listHudsonPlugins();
    }
}
