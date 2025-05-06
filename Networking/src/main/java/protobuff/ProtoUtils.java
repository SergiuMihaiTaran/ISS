package protobuff;

import java.util.ArrayList;
import java.util.List;
import Domain.Competition;
import Domain.Participant;
import Domain.User;
import dto.ParticipantDTO;
import dto.UserDTO;
import protobuff.Protobufs;

public class ProtoUtils {

    // ----- REQUEST FACTORY METHODS -----

    public static Protobufs.Request createLoginRequest(User user) {
        Protobufs.User userDTO = Protobufs.User.newBuilder()
                //.setId(user.getId())
                .setPassword(user.getPassword())
                .setName(user.getUsername())
                .build();

        return Protobufs.Request.newBuilder()
                .setType(Protobufs.Request.Type.LOGIN)
                .setUser(userDTO)
                .build();
    }

    public static Protobufs.Request createLogoutRequest(User user) {
        var userDTO = Protobufs.User.newBuilder()
                //.setId(user.getId())
                .setPassword(user.getPassword())
                .setName(user.getUsername())
                .build();

        return Protobufs.Request.newBuilder()
                .setType(Protobufs.Request.Type.LOGOUT)
                .setUser(userDTO)
                .build();
    }

    public static Protobufs.Request createGetParticipantsInCompetitionRequest(Competition competition) {
        var compDTO = Protobufs.Competition.newBuilder()
                .setId(competition.getId())
                .setStyle(competition.getStyle())
                .setDistance(competition.getDistance())
                .build();

        return Protobufs.Request.newBuilder()
                .setType(Protobufs.Request.Type.GET_PARTICIPANTS_IN_COMPETITION)
                .setCompetition(compDTO)
                .build();
    }

    public static Protobufs.Request createGetCompetitionsRequest() {
        return Protobufs.Request.newBuilder()
                .setType(Protobufs.Request.Type.GET_COMPETITION_LIST)
                .build();
    }

    public static Protobufs.Request createSaveParticipantRequest(Participant participant) {
        Protobufs.Participant.Builder partBuilder = Protobufs.Participant.newBuilder()
                //.setId(participant.getId())
                .setName(participant.getName())
                .setAge(participant.getAge());
        for (Competition comp : participant.getCompetitions()) {
            Protobufs.Competition compDTO = Protobufs.Competition.newBuilder()
                    .setId(comp.getId())
                    .setStyle(comp.getStyle())
                    .setDistance(comp.getDistance())
                    .build();

            partBuilder.addCompetitions(compDTO);
        }

        return Protobufs.Request.newBuilder()
                .setType(Protobufs.Request.Type.SAVE_PARTICIPANT)
                .setParticipant(partBuilder.build())
                .build();
    }

    public static Protobufs.Response createOkResponse() {
        return Protobufs.Response.newBuilder()
                .setType(Protobufs.Response.Type.OK)
                .build();
    }

    public static Protobufs.Response createErrorResponse(String errorMessage) {
        return Protobufs.Response.newBuilder()
                .setType(Protobufs.Response.Type.ERROR)
                .setError(errorMessage)
                .build();
    }

    public static Protobufs.Response createSavedResponse() {
        return Protobufs.Response.newBuilder()
                .setType(Protobufs.Response.Type.SAVED)
                .build();
    }

    public static Protobufs.Response createUpdateResponse() {
        return Protobufs.Response.newBuilder()
                .setType(Protobufs.Response.Type.UPDATE)
                .build();
    }

    public static Protobufs.Response createParticipantListResponse(List<Participant> participants) {
        Protobufs.Response.Builder response = Protobufs.Response.newBuilder()
                .setType(Protobufs.Response.Type.GET_PARTICIPANTS_IN_COMPETITION);

        for (Participant p : participants) {
            Protobufs.Participant partDTO = Protobufs.Participant.newBuilder()
                    .setId(p.getId())
                    .setName(p.getName())
                    .setAge(p.getAge())
                    .build();
            response.addParticipants(partDTO);
        }

        return response.build();
    }

    public static Protobufs.Response createCompetitionListResponse(List<Competition> competitions) {
        Protobufs.Response.Builder response = Protobufs.Response.newBuilder()
                .setType(Protobufs.Response.Type.GET_PARTICIPANT_LIST);

        for (Competition c : competitions) {
            Protobufs.Competition compDTO = Protobufs.Competition.newBuilder()
                    .setId(c.getId())
                    .setStyle(c.getStyle())
                    .setDistance(c.getDistance())
                    .build();
            response.addCompetitions(compDTO);
        }

        return response.build();
    }

    // ----- PARSER METHODS -----

    public static String getError(Protobufs.Response response) {
        return response.getError();
    }

    public static User getUser(Protobufs.Request request) {
        var dto = request.getUser();
        User user = new User(dto.getName(), dto.getPassword(), "");
        user.setId(dto.getId());
        return user;
    }

    public static User getUser(Protobufs.Response response) {
        var dto = response.getUser();
        User user = new User(dto.getName(), dto.getPassword(), "");
        user.setId(dto.getId());
        return user;
    }

    public static Participant getParticipant(Protobufs.Request request) {
        var dto = request.getParticipant();
        List<Competition> comps = new ArrayList<>();
        for (var compDTO : dto.getCompetitionsList()) {
            Competition comp = new Competition(compDTO.getDistance(), compDTO.getStyle());
            comp.setId(compDTO.getId());
            comps.add(comp);
        }
        Participant participant = new Participant(dto.getName(), dto.getAge(), comps);
        participant.setId(request.getCompetition().getId());  // assuming request.competition.id = participant.competitionId
        return participant;
    }

    public static Competition getCompetition(Protobufs.Request request) {
        var dto = request.getCompetition();
        Competition comp = new Competition(dto.getDistance(), dto.getStyle());
        comp.setId(dto.getId());
        return comp;
    }

    public static List<Participant> getParticipants(Protobufs.Response response) {
        List<Participant> participants = new ArrayList<>();
        for (var partDTO : response.getParticipantsList()) {
            List<Competition> comps = new ArrayList<>();
            for ( var compDTO : partDTO.getCompetitionsList()) {
                Competition comp = new Competition(compDTO.getDistance(), compDTO.getStyle());
                comp.setId(compDTO.getId());
                comps.add(comp);
            }
            Participant participant = new Participant(partDTO.getName(), partDTO.getAge(), comps);
            participant.setId(partDTO.getId());
            participants.add(participant);
        }
        return participants;
    }

    public static List<Competition> getCompetitions(Protobufs.Response response) {
        List<Competition> competitions = new ArrayList<>();
        for (var dto : response.getCompetitionsList()) {
            Competition comp = new Competition(dto.getDistance(), dto.getStyle());
            comp.setId(dto.getId());
            competitions.add(comp);
        }
        return competitions;
    }
}
