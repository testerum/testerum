import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Attachment} from "../../../../model/file/attachment.model";
import {DateUtil} from "../../../../utils/date.util";
import {AttachmentsService} from "../../../../service/attachments.service";
import {ArrayUtil} from "../../../../utils/array.util";
import {AreYouSureModalComponent} from "../../are_you_sure_modal/are-you-sure-modal.component";
import {AreYouSureModalEnum} from "../../are_you_sure_modal/are-you-sure-modal.enum";

@Component({
    selector: 'attachments-component',
    templateUrl: 'attachments.component.html',
    styleUrls: ['attachments.component.scss']
})
export class AttachmentsComponent implements OnInit {

    @Input() url: string;
    @Input() editMode: boolean;
    @Input() attachments: Array<Attachment> = [];

    @ViewChild(AreYouSureModalComponent) areYouSureModalComponent:AreYouSureModalComponent;

    constructor(private attachmentsService: AttachmentsService){}

    ngOnInit() {
    }

    onUpload(event:any) {
        let request: XMLHttpRequest = event.xhr;
        let responseAttachments: string = request.response;
        for (const responseAttachment of JSON.parse(responseAttachments)) {
            this.attachments.push(
                new Attachment().deserialize(responseAttachment)
            )
        }
    }

    getDateAsString(attachment: Attachment): string {
        return DateUtil.dateTimeToShortString(attachment.lastModifiedDate)
    }

    getFileName(attachment: Attachment): string {
        return attachment.path.fileName + "." + attachment.path.fileExtension;
    }

    isImage(attachment: Attachment): boolean {
        if(!attachment.mimeType) return false;

        return attachment.mimeType.startsWith("image/")
    }

    getAttachmentUrl(attachment: Attachment, thumbnailVersion: boolean = false): string {
        let url = "/rest/attachments?path=" + encodeURIComponent(attachment.path.toString());
        if (thumbnailVersion) {
            url += "&thumbnail=true"
        }
        return url;
    }

    delete(attachment: Attachment) {
        this.areYouSureModalComponent.show(
            "Delete Resource",
            "Are you sure you want to delete this attachment?",
            (action: AreYouSureModalEnum): void => {
                if (action == AreYouSureModalEnum.OK) {
                    this.attachmentsService.delete(attachment.path).subscribe(
                        result => {
                            ArrayUtil.removeElementFromArray(this.attachments, attachment)
                        }
                    )
                }
            }
        );
    }

    getImageIconClassBasedOnMimeType(attachment: Attachment) {

        if(!attachment.mimeType) return "fa-file";

        if(attachment.mimeType.startsWith("audio/")) return "fa-file-audio";
        if(attachment.mimeType.startsWith("video/")) return "fa-file-movie";

        switch (attachment.mimeType) {
            case 'text/plain': return 'fa-file-alt';
            case 'application/rtf': return 'fa-file-alt';
            case 'application/epub+zip': return 'fa-file-alt';

            case 'application/pdf': return 'fa-file-pdf';

            case 'application/x-cpio': return 'fa-file-archive';
            case 'application/x-shar': return 'fa-file-archive';
            case 'application/x-sbx': return 'fa-file-archive';
            case 'application/x-tar': return 'fa-file-archive';
            case 'application/x-bzip2': return 'fa-file-archive';
            case 'application/gzip': return 'fa-file-archive';
            case 'application/x-lzip': return 'fa-file-archive';
            case 'application/x-lzma': return 'fa-file-archive';
            case 'application/x-lzop': return 'fa-file-archive';
            case 'application/x-snappy-framed': return 'fa-file-archive';
            case 'application/x-xz': return 'fa-file-archive';
            case 'application/x-compress': return 'fa-file-archive';
            case 'application/x-7z-compressed': return 'fa-file-archive';
            case 'application/x-ace-compressed': return 'fa-file-archive';
            case 'application/x-astrotite-afa': return 'fa-file-archive';
            case 'application/x-alz-compressed': return 'fa-file-archive';
            case 'application/vnd.android.package-archive': return 'fa-file-archive';
            case 'application/x-arj': return 'fa-file-archive';
            case 'application/x-b1': return 'fa-file-archive';
            case 'application/vnd.ms-cab-compressed': return 'fa-file-archive';
            case 'application/x-cfs-compressed': return 'fa-file-archive';
            case 'application/x-dar': return 'fa-file-archive';
            case 'application/x-dgc-compressed': return 'fa-file-archive';
            case 'application/x-apple-diskimage': return 'fa-file-archive';
            case 'application/x-gca-compressed': return 'fa-file-archive';
            case 'application/x-lzh': return 'fa-file-archive';
            case 'application/x-lzx': return 'fa-file-archive';
            case 'application/x-rar-compressed': return 'fa-file-archive';
            case 'application/x-stuffit': return 'fa-file-archive';
            case 'application/x-stuffitx': return 'fa-file-archive';
            case 'application/x-gtar': return 'fa-file-archive';
            case 'application/zip': return 'fa-file-archive';
            case 'application/x-zoo': return 'fa-file-archive';
            case 'application/x-zip-compressed': return 'fa-file-archive';
            case 'application/x-gzip': return 'fa-file-archive';

            case 'application/msword': return 'fa-file-word';
            case 'application/vnd.openxmlformats-officedocument.wordprocessingml.document': return 'fa-file-word';
            case 'application/vnd.openxmlformats-officedocument.wordprocessingml.template': return 'fa-file-word';
            case 'application/vnd.ms-word.document.macroEnabled.12': return 'fa-file-word';
            case 'application/vnd.ms-word.template.macroEnabled.12': return 'fa-file-word';

            case 'application/vnd.ms-excel': return 'fa-file-excel';
            case 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': return 'fa-file-excel';
            case 'application/vnd.openxmlformats-officedocument.spreadsheetml.template': return 'fa-file-excel';
            case 'application/vnd.ms-excel.sheet.macroEnabled.12': return 'fa-file-excel';
            case 'application/vnd.ms-excel.template.macroEnabled.12': return 'fa-file-excel';
            case 'application/vnd.ms-excel.addin.macroEnabled.12': return 'fa-file-excel';
            case 'application/vnd.ms-excel.sheet.binary.macroEnabled.12': return 'fa-file-excel';

            case 'application/vnd.ms-powerpoint': return 'fa-file-powerpoint';
            case 'application/vnd.openxmlformats-officedocument.presentationml.presentation': return 'fa-file-powerpoint';
            case 'application/vnd.openxmlformats-officedocument.presentationml.template': return 'fa-file-powerpoint';
            case 'application/vnd.openxmlformats-officedocument.presentationml.slideshow': return 'fa-file-powerpoint';
            case 'application/vnd.ms-powerpoint.addin.macroEnabled.12': return 'fa-file-powerpoint';
            case 'application/vnd.ms-powerpoint.presentation.macroEnabled.12': return 'fa-file-powerpoint';
            case 'application/vnd.ms-powerpoint.template.macroEnabled.12': return 'fa-file-powerpoint';
            case 'application/vnd.ms-powerpoint.slideshow.macroEnabled.12': return 'fa-file-powerpoint';

            case 'text/html': return 'fa-file-code';
            case 'application/java-archive': return 'fa-file-code';
            case 'application/javascript': return 'fa-file-code';
            case 'application/json': return 'fa-file-code';
            case 'application/x-csh': return 'fa-file-code';
            case 'text/css': return 'fa-file-code';
            case 'text/csv': return 'fa-file-code';
            case 'application/ecmascript': return 'fa-file-code';
            case 'application/x-sh': return 'fa-file-code';
            case 'application/x-shockwave-flash': return 'fa-file-code';
            case 'application/typescript': return 'fa-file-code';
            case 'font/ttf': return 'fa-file-code';
            case 'application/vnd.visio': return 'fa-file-code';
            case 'application/xhtml+xml': return 'fa-file-code';
            case 'application/xml': return 'fa-file-code';
            case 'application/vnd.mozilla.xul+xml': return 'fa-file-code';
            default: return 'fa-file';
        }
    }

}
