import React, { useEffect, useState } from "react";
import styles from "./SummaryOverlay.module.css";
import api from "../api/api";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faChartPie } from "@fortawesome/free-solid-svg-icons";
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from "recharts";
import Button from "./Button";
import { CategoryMap, CategoryKey } from "../utils/categories";
import { useTheme } from "../context/ThemeProvider";

interface SummaryOverlayProps {
  teamId: string;
  visible: boolean;
  onClose: () => void;
}

interface OverviewResponse {
  totalAmount: string;
  amountByCategory: Record<CategoryKey | string, string>;
}

const DARK_COLORS = [
  "#e2f989",
  "#ff7f50",
  "#87ceeb",
  "#dda0dd",
  "#90ee90",
  "#f4a460",
  "#ff69b4",
  "#cd5c5c",
  "#00ced1",
];

const LIGHT_COLORS = [
  "#ffc107",
  "#ff6f91",
  "#66d9b3",
  "#ffa94d",
  "#8cff66",
  "#38bdf8",
  "#ff7a7a",
  "#5aa2ff",
  "#b28dff",
];
const SummaryOverlay: React.FC<SummaryOverlayProps> = ({
  teamId,
  visible,
  onClose,
}) => {
  const { theme } = useTheme();
  const [data, setData] = useState<OverviewResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!visible) return;
    api
      .get(`/me/teams/${teamId}/operations/overview`)
      .then((res) => setData(res.data))
      .catch(() => setError("Failed to load overview."));
  }, [teamId, visible]);

  if (!visible) return null;

  const chartData =
    data?.amountByCategory &&
    Object.entries(data.amountByCategory)
      .map(([key, value]) => {
        const amount = parseFloat(value);
        const labelMatch = Object.entries(CategoryMap).find(
          ([, cat]) => cat.label.toUpperCase() === key.toUpperCase(),
        );
        const category = labelMatch ? labelMatch[1] : CategoryMap.MISC;
        return {
          name: category.label,
          value: amount,
          icon: category.icon,
          rawKey: key,
        };
      })
      .sort((a, b) => b.value - a.value);

  const total = chartData?.reduce((sum, entry) => sum + entry.value, 0) || 0;
  const COLORS = theme === "dark" ? DARK_COLORS : LIGHT_COLORS;

  return (
    <div className={styles.overlay}>
      <div className={styles.modal}>
        <div className={styles.header}>
          <FontAwesomeIcon icon={faChartPie} className={styles.icon} />
          Category Summary
        </div>

        {error && <p className={styles.error}>{error}</p>}

        {data && (
          <>
            <div className={styles.total}>
              Total spent: {data.totalAmount} PLN
            </div>

            <div style={{ width: "100%", height: 400, padding: "5px" }}>
              <ResponsiveContainer>
                <PieChart>
                  <Pie
                    data={chartData}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="45%"
                    outerRadius={110}
                    innerRadius={50}
                    startAngle={90}
                    endAngle={450}
                    fill="#8884d8"
                    label
                    isAnimationActive
                  >
                    {/*
                      //@ts-ignore */}
                    {chartData?.map((entry, index) => (
                      <Cell
                        key={`cell-${index}`}
                        fill={COLORS[index % COLORS.length]}
                      />
                    ))}
                  </Pie>
                  <Tooltip />
                  <Legend
                    layout="horizontal"
                    verticalAlign="bottom"
                    align="center"
                  />
                </PieChart>
              </ResponsiveContainer>
            </div>

            <div className={styles.tableWrapper}>
              <table className={styles.categoryTable}>
                <thead>
                  <tr>
                    <th>Icon</th>
                    <th>Category</th>
                    <th>Amount</th>
                    <th>%</th>
                  </tr>
                </thead>
                <tbody>
                  {/*
                      //@ts-ignore */}
                  {chartData.map((entry) => (
                    <tr key={entry.rawKey}>
                      <td>
                        {entry.icon && (
                          <FontAwesomeIcon
                            icon={entry.icon}
                            className={styles.tableIcon}
                          />
                        )}
                      </td>
                      <td>{entry.name}</td>
                      <td>{entry.value.toFixed(2)} PLN</td>
                      <td>{((entry.value / total) * 100).toFixed(1)}%</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </>
        )}

        <div className={styles.modalButtons}>
          <Button onClick={onClose}>Close</Button>
        </div>
      </div>
    </div>
  );
};

export default SummaryOverlay;
