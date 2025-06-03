import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../api/api";
import OverlayOperation from "../../components/OverlayOperation";
import styles from "./GroupsPage.module.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { CategoryMap, CategoryKey } from "../../utils/categories";
import {
  faPlus,
  faEnvelope,
  faMoneyBill,
} from "@fortawesome/free-solid-svg-icons";
import Overlay from "../../components/Overlay";

interface Member {
  personId: number;
  fname: string;
  lname: string;
  emailAddr: string;
  defaultShare: number;
}

interface Operation {
  operationId: number;
  title: string;
  categoryName: string;
  totalAmount: number;
}

interface GroupFull {
  teamId: number;
  teamName: string;
  inviteCode: string;
  newestOperationDate: string | null;
  newestOperation: Operation | null;
  members?: Member[];
}

const GroupsPage: React.FC = () => {
  const navigate = useNavigate();

  const [groups, setGroups] = useState<GroupFull[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showCreateOverlay, setShowCreateOverlay] = useState(false);
  const [showJoinOverlay, setShowJoinOverlay] = useState(false);
  const [selectedOpId, setSelectedOpId] = useState<number | null>(null);
  const [selectedGroupId, setSelectedGroupId] = useState<string | null>(null);

  useEffect(() => {
    fetchGroups();
  }, [navigate]);

  const fetchGroups = async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await api.get<GroupFull[]>("/me/teams");
      setGroups(response.data);
    } catch (err) {
      console.error(err);
      setError("Failed to load groups.");
    } finally {
      setLoading(false);
    }
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
      await api.post(`/me/join/${inviteCode}`);
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
          groups.map((group) => (
            <div
              key={group.teamId}
              className={styles["groups-square"]}
              role="button"
              tabIndex={0}
              onClick={() => navigate(`/group/${group.teamId}`)}
              onKeyDown={(e) =>
                e.key === "Enter" && navigate(`/group/${group.teamId}`)
              }
            >
              <p className={styles["groups-simple-text"]}>{group.teamName}</p>
              <div className={styles["group-info-text"]}>
                Invite code:{" "}
                <span className={styles.code}>{group.inviteCode}</span>
              </div>
              {group.members && (
                <div className={styles["circles-container"]}>
                  {group.members.map((member) => (
                    <div key={member.personId} className={styles.circle}>
                      {member.fname.charAt(0).toUpperCase()}
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))
        )}
      </div>

      {groups.some((g) => g.newestOperation) && (
        <>
          <div className={styles["header-row"]}>
            <p className={styles.subText}>Latest transactions</p>
          </div>

          <div className={styles.latestList}>
            {groups
              .filter((g) => g.newestOperation)
              .map((group) => (
                <div
                  key={group.teamId}
                  className={styles.footer}
                  role="button"
                  tabIndex={0}
                  onClick={() => {
                    setSelectedOpId(group.newestOperation!.operationId);
                    setSelectedGroupId(group.teamId.toString());
                  }}
                  onKeyDown={(e) => {
                    if (e.key === "Enter") {
                      setSelectedOpId(group.newestOperation!.operationId);
                      setSelectedGroupId(group.teamId.toString());
                    }
                  }}
                >
                  <div className={styles.footerLeft}>
                    <div className={styles.footerTopRow}>
                      <FontAwesomeIcon
                        icon={
                          CategoryMap[
                            group.newestOperation!.categoryName.toUpperCase() as CategoryKey
                          ]?.icon ?? CategoryMap["MISC"].icon
                        }
                        className={styles.footerIcon}
                      />
                      <strong className={styles.footerTitle}>
                        {group.newestOperation!.title}
                      </strong>
                    </div>
                    <div className={styles.footerSubText}>
                      Group: {group.teamName}
                    </div>
                  </div>
                  <div className={styles.footerAmount}>
                    {group.newestOperation!.totalAmount.toFixed(2)} PLN
                  </div>
                </div>
              ))}
          </div>

          {selectedOpId && selectedGroupId && (
            <OverlayOperation
              operationId={selectedOpId}
              teamId={selectedGroupId}
              visible={true}
              onClose={() => {
                setSelectedOpId(null);
                setSelectedGroupId(null);
              }}
            />
          )}
        </>
      )}
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
