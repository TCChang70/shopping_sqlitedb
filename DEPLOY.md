# shopping_sqlitedb — SourceTree 上傳 GitHub 與 Render.com 部署步驟

---

## 目錄

- [Part 1：SourceTree 推送至 GitHub](#part-1sourcetree-推送至-github)
- [Part 2：Render.com Web Service 部署（使用 Docker）](#part-2rendercom-web-service-部署使用-docker)

---

## Part 1：SourceTree 推送至 GitHub

### Step 1：確認 / 建立 GitHub 空倉庫

1. 瀏覽器開啟 `https://github.com/new`
2. **Repository name** 輸入 `shopping_sqlitedb`
3. 不要勾選 README、.gitignore、License（本地已有完整專案）
4. 點選 **Create repository**

### Step 2：產生 PAT（Personal Access Token）

1. GitHub 右上角頭像 → **Settings**
2. 左側選單 → **Developer settings**
3. **Personal access tokens** → **Tokens (classic)**
4. 點選 **Generate new token (classic)**
5. 在 **Scopes** 勾選：
   - ✅ `repo`（完整控制 private / public repositories）
6. 拉到頁面最下方 → **Generate token**
7. **立即複製 Token**（離開頁面後無法再次查看），暫存在記事本

> ⚠️ 若使用 Fine-grained tokens，需在 Repository access 選擇此倉庫，並授予 Contents 寫入權限。

### Step 3：清除 Windows 認證快取

1. 按 `Win + R` → 輸入 `control` → 開啟控制台
2. 檢視方式改為**大圖示** → 點選**認證管理員**
3. 點選 **Windows 認證**
4. 在「一般認證」列表中，找出並**刪除**以下項目：
   - `git:https://github.com`
   - 任何名稱含有 `GitHub` 或 `adaam` 的項目
5. 全部刪除沒關係，下次 push 時會重新詢問

### Step 4：清除 SourceTree 內建認證

1. SourceTree 選單 → **工具 → 選項**
2. 切換到 **認證** 頁籤
3. 找到 GitHub 相關項目 → 點 **編輯** → **刪除**
4. 如果有多筆 GitHub 記錄，全部刪除

### Step 5：清除 Git Credential Helper（PowerShell）

以**系統管理員身分**開啟 PowerShell，執行：

```powershell
# 拒絕 GitHub 憑證（清除快取）
git credential-manager reject https://github.com

# 刪除 Windows 儲存的目標
cmdkey /delete:git:https://github.com
```

### Step 6：SourceTree 推送至 GitHub

1. 開啟 SourceTree → 載入 `C:\Java_Framework\shopping_sqlitedb`
2. 確認右上角顯示目前分支為 `main`
3. 點選工具列 **Push** 按鈕
4. 跳出 SourceTree 認證視窗，填入：

   | 欄位 | 填寫內容 |
   |-------|---------|
   | 使用者名稱 | `TCChang70`（你的 GitHub 帳號） |
   | 密碼 | 貼上 **PAT**（不是 GitHub 密碼） |

5. 選擇 **Remote Branch: main** → 按 **Push**
6. 成功後到 GitHub 頁面重新整理，確認程式碼已上傳

---

## Part 2：Render.com Web Service 部署（使用 Docker）

### 事前準備

這個專案已具備：

| 項目 | 狀態 |
|------|------|
| `Dockerfile` | ✅ 多階段建構（Maven 編譯 → JRE 執行） |
| `pom.xml` | ✅ 已包含 PostgreSQL 驅動 |
| Git 遠端 | ✅ 已指向 GitHub |

需要新增的檔案：

- `src/main/resources/application-prod.properties` — **已建立**，內容如下：

```properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.hbm2ddl.auto=update
spring.jpa.defer-datasource-initialization=true
```

> 📖 **說明**：`DATABASE_URL` 是 Render 提供的 PostgreSQL 連線環境變數，格式為 `jdbc:postgresql://user:pass@host:port/dbname`。

### Step 1：推送新增檔案至 GitHub

```bash
# 在專案目錄下執行
git add src/main/resources/application-prod.properties DEPLOY.md
git commit -m "Add prod profile for Render PostgreSQL and deploy docs"
git push origin main
```

### Step 2：Render 後台建立 Web Service

1. 登入 [Render Dashboard](https://dashboard.render.com)
2. 點選 **New +** → **Web Service**
3. 選擇 **Connect your GitHub account**（若尚未連接）
4. 授權 Render 存取 GitHub 後，搜尋並選擇 `shopping_sqlitedb`
5. 填入以下設定：

   | 欄位 | 值 |
   |------|-----|
   | Name | `shopping-sqlitedb` |
   | Runtime | **Docker**（Render 會自動偵測 Dockerfile） |
   | Region | `Singapore`（選擇離你最近的區域） |
   | Branch | `main` |
   | Plan | `Free` |

6. 展開 **Advanced** 區塊 → **Add Environment Variable**：

   | Key | Value |
   |-----|-------|
   | `SPRING_PROFILES_ACTIVE` | `prod` |

7. 點選 **Create Web Service**

### Step 3：建立 Render PostgreSQL

1. Render Dashboard → **New +** → **PostgreSQL**
2. 設定：

   | 欄位 | 值 |
   |------|-----|
   | Name | `shopping-sqlitedb-db` |
   | Database | `shoppingdb`（自訂） |
   | User | `shopping_user`（自動產生） |
   | Region | 與 Web Service 相同區域 |
   | Plan | `Free` |

3. 點選 **Create Database**
4. 建立完成後，複製 **Internal Database URL**（格式：`postgres://user:pass@host:5432/dbname`）

### Step 4：設定環境變數連接資料庫

1. 回到 Web Service Dashboard（`shopping-sqlitedb`）
2. 左側選單 → **Environment**
3. 點選 **Add Environment Variable**：

   | Key | Value |
   |-----|-------|
   | `DATABASE_URL` | `jdbc:postgresql://` + 將 Internal URL 去掉 `postgres://` 前綴後的內容 |

   > **轉換範例**：
   > Internal URL: `postgres://user:pass@host:5432/dbname`  
   > → `DATABASE_URL`: `jdbc:postgresql://user:pass@host:5432/dbname`

4. 點選 **Save Changes**

### Step 5：手動觸發部署

1. 左側選單 → **Events**
2. 點選 **Manual Deploy** → **Deploy latest commit**
3. 監看 **Build Log**：
   - Render 會偵測到 Dockerfile，開始多階段建構
   - Maven 編譯 → 產出 JAR → 包成 Docker Image
   - 預計耗時 3–8 分鐘（首次建構較久）
4. 成功後切換到 **Live Log**：
   - 確認 Spring Boot 啟動成功
   - 確認 PostgreSQL 連線正常

### Step 6：驗證服務

Render 會自動配發網域：

```
https://shopping-sqlitedb.onrender.com
```

測試方式：

```bash
# 測試首頁（依你的 Controller 路徑調整）
curl https://shopping-sqlitedb.onrender.com/

# 測試 API 健康檢查（若有 Actuator）
curl https://shopping-sqlitedb.onrender.com/actuator/health
```

---

## 常見問題

### Q1：Push 時跳出「Authentication failed」

**原因**：Windows 或 SourceTree 仍快取舊憑證。  
**解法**：重新執行 [Part 1 Step 3～5](#step-3清除-windows-認證快取)，完全清除後再 Push。

### Q2：Render 部署失敗，Log 顯示「Cannot determine embedded database driver class」

**原因**：`SPRING_PROFILES_ACTIVE=prod` 未正確設定，Spring 仍使用 SQLite 設定。  
**解法**：到 Render Environment 確認 `SPRING_PROFILES_ACTIVE=prod` 已儲存，然後手動重新部署。

### Q3：Render 部署成功但 API 回傳 500

**原因**：資料庫連線設定錯誤，或 PostgreSQL 與 Web Service 不在同一個 Region。  
**解法**：檢查 `DATABASE_URL` 格式是否正確，確認兩者 Region 一致。

### Q4：Render Free 方案的限制

- Web Service 若 15 分鐘無流量會進入睡眠；下次請求會自動喚醒（約 30 秒～1 分鐘）
- PostgreSQL Free 方案容量 1GB
- 每月 750 小時運算時數（單一服務足夠）

---

> 📝 **文件版本**：2025-06 建立
