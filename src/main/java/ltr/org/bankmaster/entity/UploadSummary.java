package ltr.org.bankmaster.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="upload_summary",schema = "common_masters")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadSummary implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_id_seq")
    @SequenceGenerator(name="file_id_seq", sequenceName = "file_id_seq",initialValue = 1, allocationSize=1)
    @Column(name="FILE_ID")
    private Long fileId;
    @Column(name="FILE_NAME")
    private String fileName;
    @Column(name="FILE_MIME_TYPE")
    private String mimeType;
    @Column(name="PROCESS_NAME")
    private String processName;
    @CreationTimestamp
    @Column(name="UPLOAD_DATE",updatable = false)
    private LocalDateTime uploadedDate;
}
