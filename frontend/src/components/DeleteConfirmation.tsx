import React from "react";
import styles from "./DeleteConfirmation.module.css";
import Button from "./Button";

interface DeleteConfirmOverlayProps {
  visible: boolean;
  onClose: () => void;
  onConfirm: () => void;
}

const DeleteConfirmOverlay: React.FC<DeleteConfirmOverlayProps> = ({
  visible,
  onClose,
  onConfirm,
}) => {
  if (!visible) return null;

  return (
    <div className={styles.backdrop}>
      <div className={styles.modal}>
        <p>
          Are you sure you want to delete this operation? This action can not be
          reverted
        </p>
        <div className={styles.buttons}>
          <Button
            className={styles.confirm}
            style={{ backgroundColor: "rgba(220, 53, 69,0.7)" }}
            onClick={() => {
              onConfirm();
              onClose();
            }}
          >
            Yes, Delete Anyway
          </Button>
          <Button
            className={styles.cancel}
            style={{ backgroundColor: "rgba(40, 167, 69,0.7)" }}
            onClick={onClose}
          >
            Cancel
          </Button>
        </div>
      </div>
    </div>
  );
};

export default DeleteConfirmOverlay;
