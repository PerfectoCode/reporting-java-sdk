package com.perfecto.reportium.imports.model.event;

import com.perfecto.reportium.imports.model.attachment.ArtifactData;

import java.util.ArrayList;
import java.util.List;

public class AddArtifactsEvent extends TestEvent {
    private List<ArtifactData> artifacts;

    public AddArtifactsEvent() {
        super(EventType.ADD_ARTIFACTS);
        artifacts = new ArrayList<>();
    }

    public AddArtifactsEvent(String externalId, String testId) {
        this();
        setExternalId(externalId);
        setTestId(testId);
    }

    public List<ArtifactData> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<ArtifactData> artifacts) {
        this.artifacts = artifacts;
    }
}
