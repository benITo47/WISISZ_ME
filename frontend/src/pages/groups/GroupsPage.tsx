import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthProvider";
import api from "../../api/api";
import styles from "./GroupsPage.module.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus, faEnvelope } from "@fortawesome/free-solid-svg-icons";
import Overlay from "../../components/Overlay"; // importujemy twój komponent overlay

interface Member {
  personId: number;
  fname: string;
  lname: string;
  emailAddr: string;
  defaultShare: number;
}

interface Group {
  id: number;
  teamName: string;
  inviteCode: string;
}

interface GroupDetails {
  teamId: number;
  teamName: string;
  members: Member[];
}

const GroupsPage: React.FC = () => {
  const { isLoggedIn, user } = useAuth();
  const navigate = useNavigate();

  const [groups, setGroups] = useState<Group[]>([]);
  const [groupDetails, setGroupDetails] = useState<
    Record<number, GroupDetails>
  >({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showCreateOverlay, setShowCreateOverlay] = useState(false);
  const [showJoinOverlay, setShowJoinOverlay] = useState(false);

  useEffect(() => {
    if (!isLoggedIn) {
      navigate("/login");
      return;
    }

    fetchGroups();
  }, [isLoggedIn, navigate]);

  const fetchGroups = async () => {
    try {
      setLoading(true);
      const response = await api.get<Group[]>("/me/teams");
      setGroups(response.data);

      const detailsMap: Record<number, GroupDetails> = {};
      const detailsPromises = response.data.map((group) =>
        api.get<GroupDetails>(`/me/teams/${group.id}`).then((res) => {
          detailsMap[group.id] = res.data;
        }),
      );

      await Promise.all(detailsPromises);
      setGroupDetails(detailsMap);
    } catch (err: any) {
      console.error(err);
      setError("Failed to load groups.");
    } finally {
      setLoading(false);
    }
  };

  const handleGroupClick = (groupId: number): void => {
    navigate(`/group/${groupId}`);
  };

  const handleCreateGroup = async (teamName: string) => {
    try {
      await api.post("/me/teams", { teamName });
      fetchGroups();
    } catch (err) {
      console.error("Failed to create group", err);
    }
  };

  const handleJoinGroup = async (inviteCode: string) => {
    try {
      await api.post("/me/teams/join", { inviteCode });
      fetchGroups();
    } catch (err) {
      console.error("Failed to join group", err);
    }
  };

  if (loading) return <div className={styles.loading}>Loading groups...</div>;
  if (error) return <div className={styles.error}>{error}</div>;

  return (
    <div className={styles["page-container"]}>
      <div className={styles["header-row"]}>
        <p className={styles.subText}>Your groups</p>
        <div className={styles.actions}>
          <div
            className={styles["icon-button"]}
            role="button"
            tabIndex={0}
            onClick={() => setShowCreateOverlay(true)}
            onKeyDown={(e) => e.key === "Enter" && setShowCreateOverlay(true)}
          >
            <FontAwesomeIcon icon={faPlus} className={styles.icon} />
          </div>
          <div
            className={styles["icon-button"]}
            role="button"
            tabIndex={0}
            onClick={() => setShowJoinOverlay(true)}
            onKeyDown={(e) => e.key === "Enter" && setShowJoinOverlay(true)}
          >
            <FontAwesomeIcon icon={faEnvelope} className={styles.icon} />
          </div>
        </div>
      </div>

      <div className={styles["squares-container"]}>
        {groups.length === 0 ? (
          <p className={styles.emptyState}>
            You don't belong to any groups yet.
          </p>
        ) : (
          groups.map((group) => {
            const details = groupDetails[group.id];
            return (
              <div
                key={group.id}
                className={styles["groups-square"]}
                role="button"
                tabIndex={0}
                onClick={() => handleGroupClick(group.id)}
                onKeyDown={(e) =>
                  e.key === "Enter" && handleGroupClick(group.id)
                }
              >
                <p className={styles["groups-simple-text"]}>{group.teamName}</p>
                <div className={styles["group-info-text"]}>
                  Invite code:{" "}
                  <span className={styles.code}>{group.inviteCode}</span>
                </div>
                {details?.members && (
                  <div className={styles["circles-container"]}>
                    {details.members.map((member) => (
                      <div key={member.personId} className={styles.circle}>
                        {member.fname.charAt(0).toUpperCase()}
                      </div>
                    ))}
                  </div>
                )}
              </div>
            );
          })
        )}
      </div>

      <Overlay
        visible={showCreateOverlay}
        onClose={() => setShowCreateOverlay(false)}
        onSubmit={handleCreateGroup}
        title="Create New Group"
        placeholder="Team name"
        submitText="Create"
      />

      <Overlay
        visible={showJoinOverlay}
        onClose={() => setShowJoinOverlay(false)}
        onSubmit={handleJoinGroup}
        title="Join Group"
        placeholder="Invitation code"
        submitText="Join"
      />
    </div>
  );
};

export default GroupsPage;
